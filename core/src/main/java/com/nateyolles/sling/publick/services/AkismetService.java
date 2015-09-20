package com.nateyolles.sling.publick.services;

import java.util.Map;

import org.apache.sling.api.resource.Resource;

/**
 * The APIs provided in order to get and set settings for the
 * Akismet Service as well as check comments for spam and to
 * submit comments as spam or ham.
 */
public interface AkismetService {

    /** OSGi property name for the domain name */
    public static final String AKISMET_DOMAIN_NAME = "akismet.domainName";

    /** OSGi property name for the API key */
    public static final String AKISMET_API_KEY = "akismet.apiKey";

    /** OSGi property name for enabled */
    public static final String AKISMET_ENABLED = "akismet.enabled";

    /**
     * Set multiple properties for the Akismet Settings service.
     *
     * This is useful for setting multiple properties as the same
     * time in that the OSGi component will only be updated once
     * and thus reset only once.
     *
     * @param properties A map of properties to set.
     * @return true if save was successful.
     */
    public boolean setProperties(final Map<String, Object> properties);

    /**
     * Is the Akismet service enabled.
     *
     * @return True if the Akismet service is enabled.
     */
    public boolean getEnabled();

    /**
     * Set whether the Akismet service is enabled.
     *
     * @param enabled The enabled property to set.
     * @return true if the save was successful.
     */
    public boolean setEnabled(final boolean enabled);

    /**
     * Get the Akismet API key.
     *
     * @return The Akismet API key.
     */
    public String getApiKey();

    /**
     * Set the Akismet API key.
     *
     * @param apiKey The API key property to set.
     * @return true if the save was successful.
     */
    public boolean setApiKey(final String apiKey);

    /**
     * Get the Akismet domain name.
     *
     * @return The Akismet domain name.
     */
    public String getDomainName();

    /**
     * Set the Akismet domain name.
     *
     * @param domainName The domain nameproperty to set.
     * @return true if the save was successful.
     */
    public boolean setDomainName(final String domainName);

    /**
     * Verify that Akismet is configured correctly using the saved
     * API key and domain name settings.
     *
     * Sends a request to the Akismet servers with the API key and
     * domain name.
     *
     * @return true if Akismet is configured correctly.
     */
    public boolean verifyKey();

    /**
     * Verify that Akismet is configured correctly using the given
     * API key and domain name.
     *
     * Sends a request to the Akismet servers with the API key and
     * domain name.
     *
     * @param apiKey The Akismet API key to test.
     * @param domainName The Akismet domain name to test.
     * @return true if Akismet is configured correctly.
     */
    public boolean verifyKey(final String apiKey, final String domainName);

    /**
     * Check comment against Akismet servers.
     *
     * Be aware that this method returns whether the submission is spam or not.
     * A false response means that the submission was successful and that the
     * comment is not spam. This behavior is inline with Akismet's behavior.
     *
     * @param commentResource The publick:comment resource to act upon.
     * @return true if comment is spam, false if comment is valid.
     */
    public boolean isSpam(final Resource commentResource);

    /**
     * Submit comment as spam to the Akismet servers.
     *
     * If a comment gets checked and incorrectly is reported as ham, this will
     * submit it back to the servers as spam to help make the world a better
     * place.
     *
     * @param commentResource The publick:comment resource to act upon.
     * @return true if submission was successful.
     */
    public boolean submitSpam(final Resource commentResource);

    /**
     * Submit comment as ham to the Akismet servers.
     *
     * If a comment gets checked and incorrectly is reported as spam, this will
     * submit it back to the servers as ham to correct a false positive.
     *
     * @param commentResource The publick:comment resource to act upon.
     * @return true if submission was successful.
     */
    public boolean submitHam(final Resource commentResource);
}