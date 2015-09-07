package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet accepts posts and sends emails on behalf of the administrator
 * with the passed in request parameters for recipient, sender and
 * message.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_PUBLIC + "/sendmail")
public class EmailServlet extends SlingAllMethodsServlet {

    /** The email service to get the server configurations. */
    @Reference
    private EmailService emailService;

    /** The reCAPTCHA service to verify the submitter isn't a robot. */
    @Reference
    private RecaptchaService recaptchaService;

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServlet.class);

    /** The email subject. */
    static final String SUBJECT = "Email submission from Publick blog engine.";

    /** The email body template. */
    static final String BODY = "From: %s <%s>\n\n%s";

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

        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();
        boolean notRobot = recaptchaService.validate(request);

        if (notRobot) {
            final String submitterName = request.getParameter("name");
            final String submitterEmail = request.getParameter("email");
            final String submitterMessage = request.getParameter("message");

            if (StringUtils.isNotEmpty(submitterName)
                    && StringUtils.isNotEmpty(submitterName)
                    && StringUtils.isNotEmpty(submitterName)) {

                final String body = String.format(BODY, submitterName, submitterEmail, submitterMessage);
                final boolean result = emailService.sendMail(SUBJECT, body);

                if (result) {
                    response.setStatus(SlingHttpServletResponse.SC_OK);
                    sendResponse(writer, SlingHttpServletResponse.SC_OK, "Email was sent successfully.");
                } else {
                    response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    sendResponse(writer, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
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
}