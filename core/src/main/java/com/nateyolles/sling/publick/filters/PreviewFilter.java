package com.nateyolles.sling.publick.filters;

import org.osgi.framework.Constants;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.engine.SlingRequestProcessor;

import javax.jcr.Session;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.UserService;
import com.nateyolles.sling.publick.http.FakeHttpServletRequest;
import com.nateyolles.sling.publick.http.FakeHttpServletResponse;

@SlingFilter(order = 0, scope = { SlingFilterScope.REQUEST })
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Internationalization Support Filter"),
    @Property(name = Constants.SERVICE_VENDOR, value = "The Apache Software Foundation")
})
public class PreviewFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewFilter.class);

    /** Service to determine if user is authorable. */
    @Reference
    private UserService userService;

    /** Service to render HTML from component. */
    @Reference
    private SlingRequestProcessor requestProcessor;

    /** Resource to inject into non-published pages */
    private static final String PREVIEW_HEADER_PATH = "/content/admin/fragments/previewHeader.html";

    /** Place to insert component in page */
    private static final String INSERTION_TAG = "<body>";

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    /**
     * Handle blog posts that are not published. If the user is authenticated,
     * display a preview bar. If the user is anonymous, seve a 404.
     *
     * @param request The Sling HTTP Servlet Request.
     * @param response The Sling HTTP Servlet Response.
     * @param chain The Filter Chain to continue processin the response.
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
            final FilterChain chain) throws IOException, ServletException {

        // Since this is a Sling Filter, the request and response objects are guaranteed
        // to be of types SlingHttpServletRequest and SlingHttpServletResponse.
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest)request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse)response;

        final Resource resource = slingRequest.getResource();
        final String method = slingRequest.getMethod();
        final String resourceType = resource.getResourceType();

        response.setCharacterEncoding(CharEncoding.UTF_8);

        if ("GET".equals(method) && PublickConstants.PAGE_TYPE_BLOG.equals(resourceType)) {

            if (!resource.getValueMap().get("visible", false)) {
                final boolean authorable = userService.isAuthorable(slingRequest.getResourceResolver().adaptTo(Session.class));

                /* If user is logged in and page isn't published, inject a preview bar. */
                if (authorable) {
                    PrintWriter out = response.getWriter();
                    CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse)response);

                    try {
                      chain.doFilter(request, responseWrapper);
                    } catch (Exception e) {
                      LOGGER.error("Could not continue chain", e);
                      chain.doFilter(request, response);
                    }

                    final String servletResponse = new String(responseWrapper.toString());
                    final String previewHeader = getPreviewHeader(slingRequest, PREVIEW_HEADER_PATH);

                    /* Insert component before body close tag. Append to end if body close tag doesn't exist. */
                    if (servletResponse != null && servletResponse.contains(INSERTION_TAG)) {
                        String[] html = servletResponse.split(INSERTION_TAG);

                        out.write(html[0] + INSERTION_TAG + previewHeader + html[1]);
                    } else {
                        out.write(servletResponse + previewHeader);
                    }
                } else {
                    /* If user is not logged in and page isn't published, forward to 404. */
                    slingResponse.sendError(SlingHttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                chain.doFilter(request, slingResponse);
            }
        } else {
            chain.doFilter(request, slingResponse);
        }
    }

    /**
     * Wrap the Response so that we can call {@link #toString()} and get
     * the full response text.
     */
    public class CharResponseWrapper extends HttpServletResponseWrapper {
        private CharArrayWriter output;

        public String toString() {
            return output.toString();
        }

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new CharArrayWriter();
        }

        public PrintWriter getWriter() {
            return new PrintWriter(output);
        }
    }

    /**
     * Get the HTML output of the component resource at the supplied path.
     *
     * @param request The current request
     * @param path The path to the component resource to inject. (e.g. /content/components/preview.html)
     * @return The HTML for the resource
     */
    private String getPreviewHeader(final SlingHttpServletRequest request, final String path) {
        String html = null;

        try {
            final FakeHttpServletRequest req = new FakeHttpServletRequest(path);
            final FakeHttpServletResponse resp = new FakeHttpServletResponse();

            requestProcessor.processRequest(req, resp, request.getResourceResolver());
            html = resp.getContent();

            //TODO: Can Sling return the resource HTML markup with adding surrounding HTML, HEAD, and BODY elements? 
            if (StringUtils.isNotBlank(html)) {
                html = html.replaceFirst("\\s*<html>\\s*<head>\\s*</head>\\s*<body>\\s*", "");
                html = html.replaceFirst("\\s*</body>\\s*</html>\\s*", "");
            }

        } catch (ServletException | IOException e) {
            LOGGER.error("Could not get component to render HTML", e);
        }

        return html;
    }
}