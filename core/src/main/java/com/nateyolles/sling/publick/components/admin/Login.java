package com.nateyolles.sling.publick.components.admin;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.auth.core.spi.AuthenticationHandler;

import com.nateyolles.sling.publick.sightly.WCMUse;

/**
 * Sightly login component to control what displays when the user
 * attempts to log in. Currently the component simply displays the
 * JAAS provided error message, but eventually will control the
 * display of the reCAPTCHA by calling the reCAPTCHA service.
 */
public class Login extends WCMUse {

    /**
     * The request.
     */
    private SlingHttpServletRequest request;

    /**
     * The JAAS provided reason that authentication failed.
     */
    private String reason;

    /**
     * Initialize this Sightly component.
     */
    @Override
    public void activate() {
        request = getRequest();
        reason = request.getParameter(AuthenticationHandler.FAILURE_REASON);
    }

    /**
     * Get the reason that authentication failed.
     *
     * @return The reason that authentication failed.
     */
    public String getReason() {
        return reason;
    }
}