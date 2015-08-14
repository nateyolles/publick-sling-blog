package com.nateyolles.sling.publick.services;

/**
 * The APIs provided in order to interact with the blog
 * system settings.
 */
public interface SystemSettingsService {

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
    boolean setBlogName(String name);

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
    boolean setExtensionlessUrls(boolean value);
}