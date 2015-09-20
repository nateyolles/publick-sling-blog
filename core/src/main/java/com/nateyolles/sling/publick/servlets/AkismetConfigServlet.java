package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.AkismetService;
import com.nateyolles.sling.publick.services.UserService;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.jcr.Session;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post servlet to save Akismet config updates.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/akismetconfig")
public class AkismetConfigServlet extends AdminServlet {

    /** Service to get and set and set Akismet settings. */
    @Reference
    private AkismetService akismetService;

    /** Service to determine if the current user has write permissions. */
    @Reference
    private UserService userService;

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AkismetConfigServlet.class);

    /** The API key request parameter */
    private static final String API_KEY_PROPERTY = "apiKey";

    /** The domain name request parameter */
    private static final String DOMAIN_NAME_PROPERTY = "domainName";

    /** The enabled request parameter */
    private static final String ENABLED_PROPERTY = "enabled";

    /**
     * Save Akismet properties on POST.
     *
     * @param request The Sling HTTP servlet request.
     * @param response The Sling HTTP servlet response.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        final PrintWriter writer = response.getWriter();
        final boolean allowWrite = userService.isAuthorable(request.getResourceResolver().adaptTo(Session.class));

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/json");

        if (allowWrite) {
            final String apiKey = request.getParameter(API_KEY_PROPERTY);
            final String domainName = request.getParameter(DOMAIN_NAME_PROPERTY);
            final boolean enabled = Boolean.parseBoolean(request.getParameter(ENABLED_PROPERTY));

            final Map<String, Object> properties = new HashMap<String, Object>();

            properties.put(AkismetService.AKISMET_DOMAIN_NAME, domainName);
            properties.put(AkismetService.AKISMET_ENABLED, enabled);

            /* Don't save the password if it's all stars. Don't save the password
             * if the user just added text to the end of the stars. This shouldn't
             * happen as the JavaScript should remove the value on focus. Save the
             * password if it's null or blank in order to clear it out. */
            if (StringUtils.isBlank(apiKey) || !apiKey.contains(PublickConstants.PASSWORD_REPLACEMENT)) {
                properties.put(AkismetService.AKISMET_API_KEY, apiKey);
            }

            final boolean result = akismetService.setProperties(properties);

            if (result) {
                response.setStatus(SlingHttpServletResponse.SC_OK);
                sendResponse(writer, "OK", "Settings successfully updated.");
            } else {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendResponse(writer, "Error", "Settings failed to update.");
            }
        } else {
            response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
            sendResponse(writer, "Error", "Current user not authorized.");
        }
    }
}