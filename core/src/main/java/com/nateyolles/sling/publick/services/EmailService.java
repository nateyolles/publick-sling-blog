package com.nateyolles.sling.publick.services;

import java.util.Map;

/**
 * API's to get and set email configurations.
 */
public interface EmailService {

    /** OSGi property name for SMTP Username */
    public static final String EMAIL_SMTP_USERNAME = "email.smtp.username";

    /** OSGi property name for SMTP Password */
    public static final String EMAIL_SMTP_PASSWORD = "email.smtp.password";

    /** OSGi property name for sender email address */
    public static final String EMAIL_SENDER = "email.sender";

    /** OSGi property name for recipient email address */
    public static final String EMAIL_RECIPIENT = "email.recipient";

    /** OSGi property name for SMTP host */
    public static final String EMAIL_SMTP_HOST = "email.smtp.host";

    /** OSGi property name for SMTP port */
    public static final String EMAIL_SMTP_PORT = "email.smtp.port";

    /**
     * Get the SMTP username.
     *
     * @return The SMTP username.
     */
    String getSmtpUsername();

    /**
     * Get the SMTP password for display use only.
     *
     * @return The SMTP password for display use only.
     */
    String getSmtpPassword();

    /**
     * Get the sender's email address.
     *
     * @return The sender's email address.
     */
    String getSender();

    /**
     * Get the recipient's email address.
     *
     * @return The recipient's email address.
     */
    String getRecipient();

    /**
     * Get the email server's host address.
     *
     * @return The email server's host address.
     */
    String getHost();

    /**
     * Get the email server's port number.
     *
     * @return The email server's port number.
     */
    Long getPort();

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
    boolean setProperties(final Map<String, Object> properties);

    /**
     * Set the SMTP username.
     *
     * @param smtpUsername The SMTP username.
     * @return true if save was successful.
     */
    boolean setSmtpUsername(final String smtpUsername);

    /**
     * Set the SMTP password.
     *
     * @param smtpPassword The SMTP password.
     * @return true if save was successful.
     */
    boolean setSmtpPassword(final String smtpPassword);

    /**
     * Set the sender's email address.
     *
     * @param sender The sender's email address.
     * @return true if save was successful.
     */
    boolean setSender(final String sender);

    /**
     * Set the recipient's email address.
     *
     * @param recipient The recipient's email address.
     * @return true if save was successful.
     */
    boolean setRecipient(final String recipient);

    /**
     * Set the email server's host address.
     *
     * @param host The email server's host address.
     * @return true if save was successful.
     */
    boolean setHost(final String host);

    /**
     * Set the email server's port number.
     *
     * @param port The email server's port number.
     * @return true if save was successful.
     */
    boolean setPort(final Long port);

    /**
     * Send an email to the recipient configured in the service.
     *
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return true if the email was sent successfully.
     */
    boolean sendMail(final String subject, final String body);

    /**
     * Send an email.
     *
     * @param recipient The recipient of the email
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return true if the email was sent successfully.
     */
    boolean sendMail(final String recipient, final String subject, final String body);
}