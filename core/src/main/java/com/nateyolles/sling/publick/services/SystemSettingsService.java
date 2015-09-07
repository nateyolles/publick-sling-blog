package com.nateyolles.sling.publick.services;

import java.util.Map;

/**
 * The APIs provided in order to interact with the blog
 * system settings.
 */
public interface SystemSettingsService {

    /** OSGi property name for the blog name */
    public static final String SYSTEM_BLOG_NAME = "system.blogName";

    /** OSGi property name for extensionless URLs */
    public static final String SYSTEM_EXTENSIONLESS_URLS = "system.extentionlessUrls";

    /**
     * Set multiple properties for the System Settings service.
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
     * Get the name of the blog.
     *
     * @return The name of the blog.
     */
    String getBlogName();

    /**
     * Set the name of the blog.
     *
     * @param name The name of the blog.
     * @return true if the save was successful.
     */
    boolean setBlogName(final String name);

    /**
     * Get the setting for extensionless URLs.
     *
     * @return The setting for extensionless URLS.
     */
    boolean getExtensionlessUrls();

    /**
     * Set the value for extensionless URLs.
     *
     * @param value The setting for extensionless URLs.
     * @return true if the save was successful.
     */
    boolean setExtensionlessUrls(final boolean value);
}