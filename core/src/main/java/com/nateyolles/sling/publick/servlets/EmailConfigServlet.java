package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.EmailService;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.servlet.ServletException;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post servlet to save email config updates.
 */
@SlingServlet(paths = "/bin/emailconfig")
public class EmailConfigServlet extends SlingAllMethodsServlet {

    @Reference
    private EmailService emailService;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConfigServlet.class);

    /**
     * The SMTP username parameter.
     */
    private static final String SMTP_USERNAME_PROPERTY = "smtpUsername";

    /**
     * The SMTP password parameter.
     */
    private static final String SMTP_PASSWORD_PROPERTY = "smtpPassword";

    /**
     * The sender parameter.
     */
    private static final String SENDER_PROPERTY = "sender";

    /**
     * The recipient parameter.
     */
    private static final String RECIPIENT_PROPERTY = "recipient";

    /**
     * The host parameter.
     */
    private static final String HOST_PROPERTY = "host";

    /**
     * The port parameter.
     */
    private static final String PORT_PROPERTY = "port";

    /**
     * Save email configuration on POST.
     *
     * @param request The Sling HTTP servlet request.
     * @param response The Sling HTTP servlet response.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        final String smtpUsername = request.getParameter(SMTP_USERNAME_PROPERTY);
        final String smtpPassword = request.getParameter(SMTP_PASSWORD_PROPERTY);
        final String sender = request.getParameter(SENDER_PROPERTY);
        final String recipient = request.getParameter(RECIPIENT_PROPERTY);
        final String host = request.getParameter(HOST_PROPERTY);
        final Long port = getPortNumber(request.getParameter(PORT_PROPERTY));

        emailService.setSmtpUsername(smtpUsername);
        emailService.setSender(sender);
        emailService.setRecipient(recipient);
        emailService.setHost(host);
        emailService.setPort(port);
        emailService.setSmtpPassword(smtpPassword);

        response.sendRedirect(PublickConstants.EMAIL_CONFIG_PATH + ".html");
    }

    /**
     * Convert the String request port parameter to a Long.
     *
     * @param port The port request parameter
     * @return The port number
     */
    private Long getPortNumber(final String port) {
        Long ret = null;

        if (port != null) {
            try {
                ret = Long.valueOf(port);
            } catch (NumberFormatException e) {
                ret = null;
            }
        }

        return ret;
    }
}