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
     * Set the public reCAPTCHA site key.
     *
     * @param sitekey The public reCAPTCHA site key.
     * @return true if the save was successful.
     */
    boolean setSiteKey(String siteKey);

    /**
     * Get the secret reCAPTCHA site key.
     *
     * @return The private reCAPTCHA "secret" key.
     */
    String getSecretKey();

    /**
     * Set the private reCAPTCHA secret key.
     *
     * @param secretkey The private reCAPTCHA secret key.
     * @return true if the save was successful.
     */
    boolean setSecretKey(String secretKey);

    /**
     * Is the reCAPTCHA service enabled.
     *
     * @return True if the reCAPTCHA service is enabled.
     */
    boolean getEnabled();

    /**
     * Set whether the reCAPTCHA service is enabled.
     *
     * @param enabled The enabled property to set.
     * @return true if the save was successful.
     */
    boolean setEnabled(boolean enabled);

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param request The SlingHttpServletRequest with the reCAPTCHA parameter
     *                  from the client-side validation.
     * @return true if not a robot
     */
    boolean validate(SlingHttpServletRequest request);

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param recaptchaResponse
     * @param remoteIP
     * @return true if not a robot
     */
    boolean validate(String recaptchaResponse, String remoteIP);
}