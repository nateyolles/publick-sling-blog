package com.nateyolles.sling.publick.services;

import javax.servlet.http.HttpServletRequest;

/**
 * The APIs provided in order to interact the HTTP helper service.
 */
public interface HttpService {

    /**
     * Get the user's IP address from the request.
     *
     * @param request The Servlet request
     * @return The IP address
     */
    String getIPAddress(final HttpServletRequest request);

    /**
     * Get the referrer from the request.
     *
     * @param request The Servlet request
     * @return The referrer
     */
    String getReferrer(final HttpServletRequest request);

    /**
     * Get the User Agent from the request.
     *
     * @param request The Servlet request
     * @return The User Agent
     */
    String getUserAgent(final HttpServletRequest request);
}