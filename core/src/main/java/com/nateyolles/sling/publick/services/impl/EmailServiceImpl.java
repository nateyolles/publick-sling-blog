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
import com.nateyolles.sling.publick.services.EmailService;

/**
 * Email service to get save email configurations.
 */
@Service( value = EmailService.class )
@Component( metatype = true, immediate = true )
public class EmailServiceImpl implements EmailService {

    /**
     * ResourceResolver factory to get a ResourceRolver.
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    /**
     * The Resource Resolver to get resources and sessions.
     */
    private ResourceResolver resolver;

    /**
     * The email configuration node where configs are saved.
     */
    private Node emailConfigNode;

    /**
     * Service activation.
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
        try {
            resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);

            emailConfigNode = JcrResourceUtil.createPath(PublickConstants.CONFIG_EMAIL_PATH, NodeType.NT_UNSTRUCTURED, NodeType.NT_UNSTRUCTURED, resolver.adaptTo(Session.class), true);
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
     * Get the SMTP username.
     *
     * @return The SMTP username.
     */
    public String getSmtpUsername() {
        return getStringProperty("smtpUsername", null);
    }

    /**
     * Get the SMTP password.
     *
     * @return The SMTP password.
     */
    public String getSmtpPassword() {
        return getStringProperty("smtpPassword", null);
    }

    /**
     * Get the sender's email address.
     *
     * @return The sender's email address.
     */
    public String getSender() {
        return getStringProperty("sender", null);
    }

    /**
     * Get the recipient's email address.
     *
     * @return The recipient's email address.
     */
    public String getRecipient() {
        return getStringProperty("recipient", null);
    }

    /**
     * Get the email server host's address.
     *
     * @return The email server host's address.
     */
    public String getHost() {
        return getStringProperty("host", null);
    }

    /**
     * Get the email server's port number.
     *
     * @return The email server's port number.
     */
    public Long getPort() {
        long port = -1;

        try {
            port = JcrUtils.getLongProperty(emailConfigNode, "port", -1);
        } catch (RepositoryException e) {
            return null;
        }

        return port >= 0 ? port : null;
    }

    /**
     * Set the SMTP username.
     *
     * @param smtpUsername The SMTP username.
     */
    public boolean setSmtpUsername(String smtpUsername){
        return setProperty("smtpUsername", smtpUsername);
    }

    /**
     * Set the SMTP password.
     *
     * @param smtpPassword The SMTP password.
     */
    public boolean setSmtpPassword(String smtpPassword){
        return setProperty("smtpPassword", smtpPassword);
    }

    /**
     * Set the sender's email address.
     *
     * @param sender The sender's email address.
     */
    public boolean setSender(String sender){
        return setProperty("sender", sender);
    }

    /**
     * Set the recipient's email address.
     *
     * @param recipient The recipient's email address.
     */
    public boolean setRecipient(String recipient){
        return setProperty("recipient", recipient);
    }

    /**
     * Set the email server's host address.
     *
     * @param host The email server's host address.
     */
    public boolean setHost(String host){
        return setProperty("host", host);
    }

    /**
     * Set the email server's port number.
     *
     * @param port The email server's port number.
     */
    public boolean setPort(Long port){
        return setProperty("port", port);
    }

    /**
     * Set a property to the email config node.
     *
     * @param name Name of the property.
     * @param value Value of the property
     * @return true if save was a success.
     */
    private boolean setProperty(String name, Object value) {
        try {
            JcrResourceUtil.setProperty(emailConfigNode, name, value);
            emailConfigNode.save();
        } catch (RepositoryException e) {
            return false;
        }

        return true;
    }

    /**
     * Get a String property from the email config node.
     *
     * @param name Name of the property to get.
     * @param value Default value if property is null.
     * @return Property value or default value.
     */
    private String getStringProperty(String name, String value) {
        try {
            return JcrUtils.getStringProperty(emailConfigNode, name, value);
        } catch (RepositoryException e) {
            return null;
        }
    }
}