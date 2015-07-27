package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.services.EmailService;
import com.nateyolles.sling.publick.services.RecaptchaService;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SlingServlet(paths = "/bin/sendmail")
public class EmailServlet extends SlingAllMethodsServlet {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServlet.class);

    /**
     * The email subject.
     */
    static final String SUBJECT = "Email submission from Publick blog engine.";

    /**
     * The email body template.
     */
    static final String BODY = "From: %s <%s>\n\n%s";

    /**
     * The reCAPTCHA service to verify the submitter isn't a robot.
     */
    @Reference
    private RecaptchaService recaptchaService;

    /**
     * The email service to get the server configurations.
     */
    @Reference
    private EmailService emailService;

    /**
     * Send email on post to servlet. The submission is verified through the reCAPTCHA
     * service before submitting. Upon completion, a JSON response is returned.
     *
     * @param request The Sling HTTP servlet request
     * @param response The Sling HTTP servlet response
     * @see <a href="http://docs.aws.amazon.com/ses/latest/DeveloperGuide/send-using-smtp-java.html">Sending an Email Through the Amazon SES SMTP Interface with Java</a>
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        // TODO: org.apache.http.entity.ContentType.APPLICATION_JSON
        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();
        boolean notRobot = recaptchaService.validate(request, getIPAddress(request));

        if (notRobot) {
            final String submitterName = request.getParameter("name");
            final String submitterEmail = request.getParameter("email");
            final String submitterMessage = request.getParameter("message");

            if (StringUtils.isNotEmpty(submitterName)
                    && StringUtils.isNotEmpty(submitterName)
                    && StringUtils.isNotEmpty(submitterName)) {
                final String sender = emailService.getSender();
                final String recipient = emailService.getRecipient();
                final String smtpUsername = emailService.getSmtpUsername();
                final String smtpPassword = emailService.getSmtpPassword();
                final String host = emailService.getHost();
                final Long port = emailService.getPort();

                final String emailBody = String.format(BODY, submitterName, submitterEmail, submitterMessage);

                // Create a Properties object to contain connection configuration information.
                Properties props = System.getProperties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.port", port);

                // Set properties indicating that we want to use STARTTLS to encrypt the connection.
                // The SMTP session will begin on an unencrypted connection, and then the client
                // will issue a STARTTLS command to upgrade to an encrypted connection.
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");

                // Create a Session object to represent a mail session with the specified properties.
                Session session = Session.getDefaultInstance(props);

                // Create a message with the specified information.
                MimeMessage msg = new MimeMessage(session);

                Transport transport = null;

                // Send the message.
                try {
                    msg.setFrom(new InternetAddress(sender));
                    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                    msg.setSubject(SUBJECT);
                    msg.setContent(emailBody, "text/plain");

                    // Create a transport.
                    transport = session.getTransport();

                    // Connect to email server using the SMTP username and password you specified above.
                    transport.connect(host, smtpUsername, smtpPassword);

                    // Send the email.
                    transport.sendMessage(msg, msg.getAllRecipients());

                    response.setStatus(SlingHttpServletResponse.SC_OK);
                    sendResponse(writer, SlingHttpServletResponse.SC_OK,
                            "Email was sent successfully.");
                } catch (Exception ex) {
                    LOGGER.error("The email was not sent.", ex);

                    response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    sendResponse(writer, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Internal server error.");
                } finally {
                    // Close and terminate the connection.
                    try {
                        transport.close();
                    } catch (MessagingException e) {
                        LOGGER.error("Could not close transport", e);
                    }
                }
            } else {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendResponse(writer, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Submission was missing the message, name or email.");
            }
        } else {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendResponse(writer, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "The reCAPTCHA service determined that you were a robot.");
        }
    }

    /**
     * Send the JSON response.
     *
     * @param writer The PrintWriter.
     * @param status The status such as 200 or 500.
     * @param message The message to send.
     */
    private void sendResponse(PrintWriter writer, int status, String message) {
        try {
            writer.write(new JSONObject()
                .put("status", status)
                .put("message", message)
                .toString());
        } catch (JSONException e) {
            LOGGER.error("Could not write JSON", e);
        }
    }

    /**
     * Get the submitter's IP address.
     *
     * @param request The SlingHttpServlet request.
     * @return The submitter's IP address.
     */
    private String getIPAddress(SlingHttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");

        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }
}