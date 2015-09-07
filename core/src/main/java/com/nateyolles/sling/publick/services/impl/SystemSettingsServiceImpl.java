package com.nateyolles.sling.publick.services.impl;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
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
    @Property(name = SystemSettingsServiceImpl.PROPERTY_BLOGNAME_NAME,
              value = SystemSettingsServiceImpl.PROPERTY_BLOGNAME_VALUE,
              label = "Blog name",
              description = "The blog name is used in the title, header, and meta tags."),
    @Property(name = SystemSettingsServiceImpl.PROPERTY_EXTENSIONLESS_URLS_NAME,
              boolValue = SystemSettingsServiceImpl.PROPERTY_EXTENSIONLESS_URLS_VALUE,
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

    /** OSGi property name for the blog name */
    public static final String PROPERTY_BLOGNAME_NAME = "system.blogName";

    /** Default value for the blog name */
    public static final String PROPERTY_BLOGNAME_VALUE = "Publick Sling + Sightly blog engine";

    /** OSGi property name for extensionless URLs */
    public static final String PROPERTY_EXTENSIONLESS_URLS_NAME = "system.extentionlessUrls";

    /** Default value for extensionless URLs */
    public static final boolean PROPERTY_EXTENSIONLESS_URLS_VALUE = false;

    /** Service activation */
    @Activate
    protected void activate(Map<String, Object> properties) {
    }

    /**
     * Get the name of the blog.
     *
     * @return The name of the blog.
     */
    public String getBlogName() {
        return osgiService.getStringProperty(COMPONENT_PID, PROPERTY_BLOGNAME_NAME, PROPERTY_BLOGNAME_VALUE);
    }

    /**
     * Set the name of the blog.
     *
     * @param name The name of the blog.
     * @return true if the save was successful.
     */
    public boolean setBlogName(String name) {
        return osgiService.setProperty(COMPONENT_PID, PROPERTY_BLOGNAME_NAME, name);
    }

    /**
     * Get the setting for extensionless URLs.
     *
     * @return The setting for extensionless URLS.
     */
    public boolean getExtensionlessUrls() {
        return osgiService.getBooleanProperty(COMPONENT_PID, PROPERTY_EXTENSIONLESS_URLS_NAME, PROPERTY_EXTENSIONLESS_URLS_VALUE);
    }

    /**
     * Set the value for extensionless URLs.
     *
     * @param value The setting for extensionless URLs.
     * @return true if the save was successful.
     */
    public boolean setExtensionlessUrls(boolean value) {
        return osgiService.setProperty(COMPONENT_PID, PROPERTY_EXTENSIONLESS_URLS_NAME, value);
    }
}