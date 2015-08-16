package com.nateyolles.sling.publick.filters;

import org.apache.commons.lang.CharEncoding;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.SlingHttpServletRequest;

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

import com.nateyolles.sling.publick.services.LinkRewriterService;

/**
 * Filter all GET requests for HTML files and updates the all anchor elements'
 * links with the {@link com.nateyolles.sling.publick.services.LinkRewriterService}
 * for extensionless URLs depending on the System Settings configured by the user.
 */
@Component(immediate = true, metatype = true)
@Service(Filter.class)
@Properties({
    @Property(name="service.pid", value="com.nateyolles.sling.publick.filters.LinkRewriterFilter", propertyPrivate=false),
    @Property(name="service.description", value="LinkRewriterFilter", propertyPrivate=false),
    @Property(name="service.vendor", value="Publick", propertyPrivate=false),
    @Property(name="filter.scope", value="request"),
    @Property(name="sling.filter.scope", value="request"),
    @Property(name="service.ranking", intValue=100001)
})
public class LinkRewriterFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkRewriterFilter.class);

    /**
     * The link rewriter service to remove extensions from URLs.
     */
    @Reference
    private LinkRewriterService linkRewriter;

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    /**
     * Filter all GET requests for HTML pages.
     *
     * Wrap the request and convert the response to a String where we can pass
     * it to the Link Rewriter service.
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
        final String path = slingRequest.getPathInfo().toLowerCase();
        final String host = slingRequest.getServerName();
        final String method = slingRequest.getMethod();

        response.setCharacterEncoding(CharEncoding.UTF_8);

        if (linkRewriter != null && "GET".equals(method) && path.endsWith(".html")) {
            PrintWriter out = response.getWriter();
            CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse)response);

            try {
              chain.doFilter(request, responseWrapper);
            } catch (Exception e) {
              LOGGER.error("Could not continue chain", e);
              chain.doFilter(request, response);
            }

            String servletResponse = new String(responseWrapper.toString());

            out.write(linkRewriter.rewriteAllLinks(servletResponse, host));
        } else {
          chain.doFilter(request, response);
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
}