package com.nateyolles.sling.publick.services;

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
}