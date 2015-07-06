package com.nateyolles.sling.publick.components.admin;

import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.PublickConstants;

public class RecaptchaConfig implements Use {

    private SlingHttpServletRequest request;
    private String siteKey;
    private String secretKey;
    private boolean enabled;

    @Override
    public void init(Bindings bindings) {
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
        ResourceResolver resolver = request.getResourceResolver();

        Resource recaptcha = resolver.getResource(PublickConstants.CONFIG_RECAPTCHA_PATH);

        if (recaptcha != null) {
            ValueMap properties = recaptcha.adaptTo(ValueMap.class);

            siteKey = properties.get("siteKey", String.class);
            secretKey = properties.get("secretKey", String.class);
            enabled = properties.get("enabled", Boolean.class);
        }
    }

    public String getSiteKey() {
        return siteKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public boolean getEnabled() {
        return enabled;
    }
}