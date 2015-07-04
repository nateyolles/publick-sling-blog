package com.nateyolles.sling.publick.components.admin;

import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.apache.sling.auth.core.spi.AuthenticationHandler;

import com.nateyolles.sling.publick.PublickConstants;

public class Login implements Use {

    private SlingHttpServletRequest request;
    private String reason;

    @Override
    public void init(Bindings bindings) {
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
        reason = request.getParameter(AuthenticationHandler.FAILURE_REASON);
    }

    public String getReason() {
        return reason;
    }
}