package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.EmailService;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post servlet to save email config updates.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/emailconfig")
public class EmailConfigServlet extends AdminServlet {

    /** Service to get and set email configurations. */
    @Reference
    private EmailService emailService;

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConfigServlet.class);

    /** The SMTP username parameter. */
    private static final String SMTP_USERNAME_PROPERTY = "smtpUsername";

    /** The SMTP password parameter. */
    private static final String SMTP_PASSWORD_PROPERTY = "smtpPassword";

    /** The sender parameter. */
    private static final String SENDER_PROPERTY = "sender";

    /** The recipient parameter. */
    private static final String RECIPIENT_PROPERTY = "recipient";

    /** The host parameter. */
    private static final String HOST_PROPERTY = "host";

    /** The port parameter. */
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

        final boolean resultUser = emailService.setSmtpUsername(smtpUsername);
        final boolean resultSender = emailService.setSender(sender);
        final boolean resultRecipient = emailService.setRecipient(recipient);
        final boolean resultHost = emailService.setHost(host);
        final boolean resultPort = emailService.setPort(port);
        final boolean resultPass;

        /* Don't save the password if it's all stars. Don't save the password
         * if the user just added text to the end of the stars. This shouldn't
         * happen as the JavaScript should remove the value on focus. Save the
         * password if it's null or blank in order to clear it out. */
        if (smtpPassword != null && smtpPassword.contains(PublickConstants.PASSWORD_REPLACEMENT)) {
            resultPass = true;
        } else {
            resultPass = emailService.setSmtpPassword(smtpPassword);
        }

        final PrintWriter writer = response.getWriter();

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/json");

        if (resultUser && resultSender && resultRecipient && resultHost
                && resultHost && resultPort && resultPass) {
            response.setStatus(SlingHttpServletResponse.SC_OK);
            sendResponse(writer, "OK", "Settings successfully updated.");
        } else {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendResponse(writer, "Error", "Settings failed to update.");
        }
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