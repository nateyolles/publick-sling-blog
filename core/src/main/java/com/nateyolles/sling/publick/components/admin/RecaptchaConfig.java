package com.nateyolles.sling.publick.components.admin;

import javax.script.Bindings;

import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.RecaptchaService;

public class RecaptchaConfig implements Use {

    private RecaptchaService recaptchaService;

    private SlingScriptHelper scriptHelper;
    private String siteKey;
    private String secretKey;
    private boolean enabled;

    @Override
    public void init(Bindings bindings) {
        scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);

        recaptchaService = scriptHelper.getService(RecaptchaService.class);

        if (recaptchaService != null) {
            siteKey = recaptchaService.getSiteKey();
            secretKey = recaptchaService.getSecretKey();
            enabled = recaptchaService.getEnabled();
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