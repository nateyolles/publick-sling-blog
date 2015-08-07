package com.nateyolles.sling.publick.services;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * The APIs provided in order to create the reCAPTCHA service to
 * provide the reCAPTCHA keys and communicate with the Google
 * server to provide reCAPTCHA authentication.
 */
public interface RecaptchaService {

    /**
     * Get the public reCAPTCHA site key.
     *
     * @return the public reCAPTCHA "site" key.
     */
    String getSiteKey();

    /**
     * Get the secret reCAPTCHA site key.
     *
     * @return The private reCAPTCHA "secret" key.
     */
    String getSecretKey();

    /**
     * Is the reCAPTCHA service enabled.
     *
     * @return True if the reCAPTCHA service is enabled.
     */
    boolean getEnabled();

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param request The SlingHttpServletRequest with the reCAPTCHA parameter
     *                  from the client-side validation.
     * @return true if not a robot
     */
    public boolean validate(SlingHttpServletRequest request);

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param recaptchaResponse
     * @param remoteIP
     * @return true if not a robot
     */
    boolean validate(String recaptchaResponse, String remoteIP);
}