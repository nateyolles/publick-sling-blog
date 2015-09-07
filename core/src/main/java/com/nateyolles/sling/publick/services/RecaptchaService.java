package com.nateyolles.sling.publick.services;

import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * The APIs provided in order to create the reCAPTCHA service to
 * provide the reCAPTCHA keys and communicate with the Google
 * server to provide reCAPTCHA authentication.
 */
public interface RecaptchaService {

    /** OSGi property name for the site key */
    public static final String RECAPTCHA_SITE_KEY = "recaptcha.siteKey";

    /** OSGi property name for the secret key */
    public static final String RECAPTCHA_SECRET_KEY = "recaptcha.secretKey";

    /** OSGi property name for enabled */
    public static final String RECAPTCHA_ENABLED = "recaptcha.enabled";

    /**
     * Set multiple properties for the reCAPTCHA Settings service.
     *
     * This is useful for setting multiple properties as the same
     * time in that the OSGi component will only be updated once
     * and thus reset only once.
     *
     * @param properties A map of properties to set.
     * @return true if save was successful.
     */
    boolean setProperties(final Map<String, Object> properties);

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
    boolean setSiteKey(final String siteKey);

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
    boolean setSecretKey(final String secretKey);

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
    boolean setEnabled(final boolean enabled);

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param request The SlingHttpServletRequest with the reCAPTCHA parameter
     *                  from the client-side validation.
     * @return true if not a robot
     */
    boolean validate(final SlingHttpServletRequest request);

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param recaptchaResponse
     * @param remoteIP
     * @return true if not a robot
     */
    boolean validate(final String recaptchaResponse, final String remoteIP);
}