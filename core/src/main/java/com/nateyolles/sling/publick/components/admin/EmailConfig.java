package com.nateyolles.sling.publick.components.admin;

import org.apache.sling.api.scripting.SlingScriptHelper;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.EmailService;
import com.nateyolles.sling.publick.sightly.WCMUse;

/**
 * Sightly component to update and save email server configurations.
 */
public class EmailConfig extends WCMUse {

    /**
     * The Sling Script Helper to get services.
     */
    private SlingScriptHelper scriptHelper;

    /**
     * The EmailService to get the saved email configurations.
     */
    private EmailService emailService;

    /**
     * Initialize the Sightly component.
     */
    @Override
    public void activate() {
        scriptHelper = getSlingScriptHelper();

        emailService = scriptHelper.getService(EmailService.class);
    }

    /**
     * Get the email server SMTP username.
     *
     * @return The email server SMTP username.
     */
    public String getSmtpUsername() {
        return emailService.getSmtpUsername();
    }

    /**
     * Get the email server SMTP password.
     *
     * @return The email server SMTP password.
     */
    public String getSmtpPassword() {
        return emailService.getSmtpPassword();
    }

    /**
     * Get the sender email address.
     *
     * @return The sender's email address.
     */
    public String getSender() {
        return emailService.getSender();
    }

    /**
     * Get the recipient email address.
     *
     * @return The recipient's email address.
     */
    public String getRecipient() {
        return emailService.getRecipient();
    }

    /**
     * Get the email server's host address.
     *
     * @return The email server's host address.
     */
    public String getHost() {
        return emailService.getHost();
    }

    /**
     * Get the email server's port number.
     *
     * @return The email server's port number.
     */
    public Long getPort() {
        return emailService.getPort();
    }
}