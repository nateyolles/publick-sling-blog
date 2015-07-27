package com.nateyolles.sling.publick.services;

/**
 * API's to get and set email configurations.
 */
public interface EmailService {

    /**
     * Get the SMTP username.
     *
     * @return The SMTP username.
     */
    String getSmtpUsername();

    /**
     * Get the SMTP password.
     *
     * @return The SMTP password.
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
     * Set the SMTP username.
     *
     * @param smtpUsername The SMTP username.
     * @return true if save was successful.
     */
    boolean setSmtpUsername(String smtpUsername);

    /**
     * Set the SMTP password.
     *
     * @param smtpPassword The SMTP password.
     * @return true if save was successful.
     */
    boolean setSmtpPassword(String smtpPassword);

    /**
     * Set the sender's email address.
     *
     * @param sender The sender's email address.
     * @return true if save was successful.
     */
    boolean setSender(String sender);

    /**
     * Set the recipient's email address.
     *
     * @param recipient The recipient's email address.
     * @return true if save was successful.
     */
    boolean setRecipient(String recipient);

    /**
     * Set the email server's host address.
     *
     * @param host The email server's host address.
     * @return true if save was successful.
     */
    boolean setHost(String host);

    /**
     * Set the email server's port number.
     *
     * @param port The email server's port number.
     * @return true if save was successful.
     */
    boolean setPort(Long port);
}