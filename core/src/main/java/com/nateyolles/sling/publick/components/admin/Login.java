package com.nateyolles.sling.publick.components.admin;

import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.apache.sling.auth.core.spi.AuthenticationHandler;

/**
 * Sightly login component to control what displays when the user
 * attempts to log in. Currently the component simply displays the
 * JAAS provided error message, but eventually will control the
 * display of the reCAPTCHA by calling the reCAPTCHA service.
 */
public class Login implements Use {

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
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
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