package com.nateyolles.sling.publick.services.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.OsgiConfigurationService;
import com.nateyolles.sling.publick.services.SystemSettingsService;

import org.osgi.framework.Constants;

/**
 * System settings configuration to save blog engine settings
 * such as blog name and extensionless URLs.
 */
@Service(value = SystemSettingsService.class)
@Component(metatype = true,
           immediate = true,
           name = "Publick system settings",
           description = "General blog engine system settings.")
@Properties({
    @Property(name = SystemSettingsServiceImpl.SYSTEM_BLOG_NAME,
              value = SystemSettingsServiceImpl.BLOG_NAME_DEFAULT_VALUE,
              label = "Blog name",
              description = "The blog name is used in the title, header, and meta tags."),
    @Property(name = SystemSettingsServiceImpl.SYSTEM_EXTENSIONLESS_URLS,
              boolValue = SystemSettingsServiceImpl.EXTENSIONLESS_URLS_DEFAULT_VALUE,
              label = "Extentionless URLs",
              description = "Enabling extenionless URLs alters links written by the blog engine. "
                      + "You must also have corresponding web server redirects in place."),
    @Property(name = Constants.SERVICE_DESCRIPTION,
              value = "General blog engine system settings."),
    @Property(name = Constants.SERVICE_VENDOR,
              value = "Publick")
})
public class SystemSettingsServiceImpl implements SystemSettingsService {

    /** Service to get and set OSGi properties. */
    @Reference
    private OsgiConfigurationService osgiService;

    /** The logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemSettingsService.class);

    /** PID of the current OSGi component */
    private static final String COMPONENT_PID = "Publick system settings";

    /** Default value for the blog name */
    public static final String BLOG_NAME_DEFAULT_VALUE = "Publick Sling + Sightly blog engine";

    /** Default value for extensionless URLs */
    public static final boolean EXTENSIONLESS_URLS_DEFAULT_VALUE = false;

    /** Service activation */
    @Activate
    protected void activate(Map<String, Object> properties) {
    }

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
    public boolean setProperties(final Map<String, Object> properties) {
        return osgiService.setProperties(COMPONENT_PID, properties);
    }

    /**
     * Get the name of the blog.
     *
     * @return The name of the blog.
     */
    public String getBlogName() {
        return osgiService.getStringProperty(COMPONENT_PID, SYSTEM_BLOG_NAME, BLOG_NAME_DEFAULT_VALUE);
    }

    /**
     * Set the name of the blog.
     *
     * @param name The name of the blog.
     * @return true if the save was successful.
     */
    public boolean setBlogName(final String name) {
        return osgiService.setProperty(COMPONENT_PID, SYSTEM_BLOG_NAME, name);
    }

    /**
     * Get the setting for extensionless URLs.
     *
     * @return The setting for extensionless URLS.
     */
    public boolean getExtensionlessUrls() {
        return osgiService.getBooleanProperty(COMPONENT_PID, SYSTEM_EXTENSIONLESS_URLS, EXTENSIONLESS_URLS_DEFAULT_VALUE);
    }

    /**
     * Set the value for extensionless URLs.
     *
     * @param value The setting for extensionless URLs.
     * @return true if the save was successful.
     */
    public boolean setExtensionlessUrls(final boolean value) {
        return osgiService.setProperty(COMPONENT_PID, SYSTEM_EXTENSIONLESS_URLS, value);
    }
}