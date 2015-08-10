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
}