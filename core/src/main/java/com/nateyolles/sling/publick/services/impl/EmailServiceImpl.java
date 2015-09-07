package com.nateyolles.sling.publick.services.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.EmailService;
import com.nateyolles.sling.publick.services.OsgiConfigurationService;

/**
 * Email service to get and set SMTP properties.
 */
@Service(value = EmailService.class)
@Component(metatype = true,
           immediate = true,
           name = "Publick email settings",
           description = "SMTP settings and email services.")
@Properties({
    @Property(name = EmailServiceImpl.EMAIL_SMTP_USERNAME,
              label = "SMTP Username",
              description = "The username to access your SMTP server."),
    @Property(name = EmailServiceImpl.EMAIL_SMTP_PASSWORD,
              label = "SMTP Password",
              description = "The password to access your SMTP server."),
    @Property(name = EmailServiceImpl.EMAIL_SENDER,
              label = "Sender Email",
              description = "The email address to send from. (e.g. sender@mailserver.com)"),
    @Property(name = EmailServiceImpl.EMAIL_RECIPIENT,
              label = "Recipient Email",
              description = "The email address to send to. (e.g. recipient@mailserver.com)"),
    @Property(name = EmailServiceImpl.EMAIL_SMTP_HOST,
              label = "Host",
              description = "The SMTP host server (e.g. email-smtp.us-west-2.amazonaws.com)"),
    @Property(name = EmailServiceImpl.EMAIL_SMTP_PORT,
              label = "Port",
              description = "The SMTP host server port (e.g. 25)"),
    @Property(name = Constants.SERVICE_DESCRIPTION,
              value = "SMTP settings and email services."),
    @Property(name = Constants.SERVICE_VENDOR,
              value = "Publick")
})
public class EmailServiceImpl implements EmailService {

    /** Service to get and set OSGi properties. */
    @Reference
    private OsgiConfigurationService osgiService;

    /** PID of the current OSGi component */
    private static final String COMPONENT_PID = "Publick email settings";

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    /**
     * Service activation.
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
    }

    /**
     * Service Deactivation.
     *
     * @param ctx The current component context.
     */
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
    }

    /**
     * Get the SMTP username.
     *
     * @return The SMTP username.
     */
    public String getSmtpUsername() {
        return osgiService.getStringProperty(COMPONENT_PID, EMAIL_SMTP_USERNAME, null);
    }

    /**
     * Get the SMTP password.
     *
     * @return The SMTP password.
     */
    public String getSmtpPassword() {
        String password = osgiService.getStringProperty(COMPONENT_PID, EMAIL_SMTP_PASSWORD, null);

        return StringUtils.isNotBlank(password) ? PublickConstants.PASSWORD_REPLACEMENT : null;
    }

    /**
     * Get the sender's email address.
     *
     * @return The sender's email address.
     */
    public String getSender() {
        return osgiService.getStringProperty(COMPONENT_PID, EMAIL_SENDER, null);
    }

    /**
     * Get the recipient's email address.
     *
     * @return The recipient's email address.
     */
    public String getRecipient() {
        return osgiService.getStringProperty(COMPONENT_PID, EMAIL_RECIPIENT, null);
    }

    /**
     * Get the email server host's address.
     *
     * @return The email server host's address.
     */
    public String getHost() {
        return osgiService.getStringProperty(COMPONENT_PID, EMAIL_SMTP_HOST, null);
    }

    /**
     * Get the email server's port number.
     *
     * @return The email server's port number.
     */
    public Long getPort() {
        return osgiService.getLongProperty(COMPONENT_PID, EMAIL_SMTP_PORT, null);
    }

    /**
     * Set multiple properties for the Email service.
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
     * Set the SMTP username.
     *
     * @param smtpUsername The SMTP username.
     */
    public boolean setSmtpUsername(final String smtpUsername){
        return osgiService.setProperty(COMPONENT_PID, EMAIL_SMTP_USERNAME, smtpUsername);
    }

    /**
     * Set the SMTP password.
     *
     * @param smtpPassword The SMTP password.
     */
    public boolean setSmtpPassword(final String smtpPassword){
        return osgiService.setProperty(COMPONENT_PID, EMAIL_SMTP_PASSWORD, smtpPassword);
    }

    /**
     * Set the sender's email address.
     *
     * @param sender The sender's email address.
     */
    public boolean setSender(final String sender){
        return osgiService.setProperty(COMPONENT_PID, EMAIL_SENDER, sender);
    }

    /**
     * Set the recipient's email address.
     *
     * @param recipient The recipient's email address.
     */
    public boolean setRecipient(final String recipient){
        return osgiService.setProperty(COMPONENT_PID, EMAIL_RECIPIENT, recipient);
    }

    /**
     * Set the email server's host address.
     *
     * @param host The email server's host address.
     */
    public boolean setHost(final String host){
        return osgiService.setProperty(COMPONENT_PID, EMAIL_SMTP_HOST, host);
    }

    /**
     * Set the email server's port number.
     *
     * @param port The email server's port number.
     */
    public boolean setPort(final Long port){
        return osgiService.setProperty(COMPONENT_PID, EMAIL_SMTP_PORT, port);
    }
}