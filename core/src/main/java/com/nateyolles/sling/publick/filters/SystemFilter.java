package com.nateyolles.sling.publick.filters;

import org.apache.commons.lang.CharEncoding;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.jcr.Session;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.UserService;

/**
 * Prevent unauthorables from accessing Sling's user admin.
 */
@Component(immediate = true, metatype = true)
@Service(Filter.class)
@Properties({
    @Property(name="service.pid", value="com.nateyolles.sling.publick.filters.SystemFilter", propertyPrivate=false),
    @Property(name="service.description", value="Prevent unauthorables from accessing Sling's user admin.", propertyPrivate=false),
    @Property(name="service.vendor", value="Publick", propertyPrivate=false),
    @Property(name="filter.scope", value="request"),
    @Property(name="sling.filter.scope", value="request"),
    @Property(name="service.ranking", intValue=100001)
})
public class SystemFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemFilter.class);

    /** Service to determine if user is authorable. */
    @Reference
    private UserService userService;

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    /**
     * Prevent unauthorables from accessing Sling's user admin.
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
        final String path = slingRequest.getPathInfo().toLowerCase();
        final String method = slingRequest.getMethod();

        response.setCharacterEncoding(CharEncoding.UTF_8);

        if ("POST".equals(method) && path.startsWith("/system")) {
            if (userService != null) {
                final boolean allow = userService.isAuthorable(slingRequest.getResourceResolver().adaptTo(Session.class));

                if (allow) {
                    chain.doFilter(request, response);
                } else {
                    slingResponse.sendError(SlingHttpServletResponse.SC_FORBIDDEN);
                }
            } else {
                slingResponse.sendError(SlingHttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}