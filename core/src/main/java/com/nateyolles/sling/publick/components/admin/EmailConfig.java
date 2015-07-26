package com.nateyolles.sling.publick.components.admin;

import javax.script.Bindings;

import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.services.EmailService;

/**
 * Sightly component to update and save email server configurations.
 */
public class EmailConfig implements Use {

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
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);

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