package com.nateyolles.sling.publick.services.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpHeaders;

import org.osgi.service.component.ComponentContext;

import com.nateyolles.sling.publick.services.HttpService;

/**
 * Helper methods for working with HTTP requests and responses.
 */
@Service(value = HttpService.class)
@Component(immediate = true,
           name = "Publick HTTP helper service",
           description = "HTTP helper methods for HTTP requests and responses.")
public class HttpServiceImpl implements HttpService {

    /**
     * Get the user's IP address from the request.
     *
     * @param request The Servlet request
     * @return The IP address
     */
    public String getIPAddress(final HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");

        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    /**
     * Get the referrer from the request.
     *
     * @param request The Servlet request
     * @return The referrer
     */
    public String getReferrer(final HttpServletRequest request) {
        return request.getHeader(HttpHeaders.REFERER);
    }

    /**
     * Get the User Agent from the request.
     *
     * @param request The Servlet request
     * @return The User Agent
     */
    public String getUserAgent(final HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    /**
     * Service activation.
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
    }

    /**
     * Service Deactivation.
     *
     * @param ctx The current component context.
     */
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
    }
}