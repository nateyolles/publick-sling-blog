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
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceUtil;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.SystemSettingsService;

/**
 * System settings configuration to save blog engine settings
 * such as blog name and extensionless URLs.
 */
@Service( value = SystemSettingsService.class )
@Component( metatype = true, immediate = true )
public class SystemSettingsServiceImpl implements SystemSettingsService {

    /**
     * ResourceResolverFactory to get a ResourceRolver.
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaServiceImpl.class);

    /**
     * The Resource Resolver to get resources and sessions.
     */
    private ResourceResolver resolver;

    /**
     * The system configuration node where configs are saved.
     */
    private Node systemConfigNode;

    /**
     * Service activation.
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
        try {
            resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);

            systemConfigNode = JcrResourceUtil.createPath(PublickConstants.CONFIG_SYSTEM_PATH, NodeType.NT_UNSTRUCTURED, NodeType.NT_UNSTRUCTURED, resolver.adaptTo(Session.class), true);
        } catch (LoginException e) {
            LOGGER.error("Could not get resource resolver.", e);
        } catch (RepositoryException e) {
            LOGGER.error("Could not login into repository", e);
        }
    }

    /**
     * Service Deactivation.
     *
     * @param ctx The current component context.
     */
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        if (resolver != null && resolver.isLive()) {
            resolver.close();
            resolver = null;
        }
    }

    /**
     * Get the name of the blog.
     *
     * @return The name of the blog.
     */
    public String getBlogName() {
        try {
            return JcrUtils.getStringProperty(systemConfigNode, "blogName", null);
        } catch (RepositoryException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Set the name of the blog.
     *
     * @param name The name of the blog.
     * @return true if the save was successful.
     */
    public boolean setBlogName(String name) {
        try {
            JcrResourceUtil.setProperty(systemConfigNode, "blogName", name);
            systemConfigNode.save();
        } catch (RepositoryException e) {
            return false;
        }

        return true;
    }

    /**
     * Get the setting for extensionless URLs.
     *
     * @return The setting for extensionless URLS.
     */
    public boolean getExtensionlessUrls() {
        try {
            return JcrUtils.getBooleanProperty(systemConfigNode, "extensionlessUrls", false);
        } catch (RepositoryException e) {
            return false;
        }
    }

    /**
     * Set the value for extensionless URLs.
     *
     * @param value The setting for extensionless URLs.
     * @return true if the save was successful.
     */
    public boolean setExtensionlessUrls(boolean value) {
        try {
            JcrResourceUtil.setProperty(systemConfigNode, "extensionlessUrls", value);
            systemConfigNode.save();
        } catch (RepositoryException e) {
            return false;
        }

        return true;
    }
}