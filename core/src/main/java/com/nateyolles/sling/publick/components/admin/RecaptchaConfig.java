package com.nateyolles.sling.publick.components.admin;

import org.apache.sling.api.scripting.SlingScriptHelper;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.RecaptchaService;
import com.nateyolles.sling.publick.sightly.WCMUse;

/**
 * Sightly component to update and save reCAPTCHA configurations. Two
 * keys are provided by Google when you sign up for the reCAPTCHA
 * service. The site key is delivered with the form to be submitted
 * while the secret key is used in the server side communication with
 * Google to verify the form submission wasn't hacked. It's not
 * critical to keep the "secret" key as secure as password for example.
 */
public class RecaptchaConfig extends WCMUse {

    /**
     * The reCAPTCHA service to save the config properties.
     */
    private RecaptchaService recaptchaService;

    /**
     * The Sling Script Helper to get services.
     */
    private SlingScriptHelper scriptHelper;

    /**
     * The reCAPTCHA site key.
     */
    private String siteKey;

    /**
     * The reCAPTCHA secret key.
     */
    private String secretKey;

    /**
     * Determines if the reCAPTCHA should be enabled even if the
     * site and secret keys are provided.
     */
    private boolean enabled;

    /**
     * Initialize the Sightly component.
     *
     * @param bindings The current execution context.
     */
    @Override
    public void activate() {
        scriptHelper = getSlingScriptHelper();

        recaptchaService = scriptHelper.getService(RecaptchaService.class);

        if (recaptchaService != null) {
            siteKey = recaptchaService.getSiteKey();
            secretKey = isAuthorable() ? recaptchaService.getSecretKey() : PublickConstants.PASSWORD_REPLACEMENT;
            enabled = recaptchaService.getEnabled();
        }
    }

    /**
     * Get the reCAPTCHA site key.
     *
     * @return The reCAPTCHA site key.
     */
    public String getSiteKey() {
        return siteKey;
    }

    /**
     * Get the reCAPTCHA secret key.
     *
     * @return The reCAPTCHA secret key.
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Is reCAPTCHA enabled.
     *
     * @return True if reCAPTCHA is enabled.
     */
    public boolean getEnabled() {
        return enabled;
    }
}