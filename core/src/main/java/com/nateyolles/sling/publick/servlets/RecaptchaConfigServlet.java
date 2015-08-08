package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post servlet to save reCAPTCHA config updates.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/recaptchaconfig")
public class RecaptchaConfigServlet extends SlingAllMethodsServlet {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaConfigServlet.class);

    /**
     * The site key property of the reCAPTCHA config node.
     */
    private static final String SITE_KEY_PROPERTY = "siteKey";

    /**
     * The secret key property of the reCAPTCHA config node.
     */
    private static final String SECRET_KEY_PROPERTY = "secretKey";

    /**
     * The enabled property of the reCAPTCHA config node.
     */
    private static final String ENABLED_PROPERTY = "enabled";

    /**
     * Save reCAPTCHA properties on POST.
     *
     * @param request The Sling HTTP servlet request.
     * @param response The Sling HTTP servlet response.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        final String siteKey = request.getParameter(SITE_KEY_PROPERTY);
        final String secretKey = request.getParameter(SECRET_KEY_PROPERTY);
        final boolean enabled = Boolean.parseBoolean(request.getParameter(ENABLED_PROPERTY));
        final Map<String, Object> newProperties = new HashMap<String, Object>(){{
            put(SITE_KEY_PROPERTY, siteKey);
            put(SECRET_KEY_PROPERTY, secretKey);
            put(ENABLED_PROPERTY, enabled);
        }};

        ResourceResolver resolver = null;

        try {
            resolver = request.getResourceResolver();
            Resource recaptcha = resolver.getResource(PublickConstants.CONFIG_RECAPTCHA_PATH);

            if (recaptcha != null) {
                ModifiableValueMap properties = recaptcha.adaptTo(ModifiableValueMap.class);
                properties.putAll(newProperties);
                resolver.commit();
            }
        } catch (PersistenceException e) {
            LOGGER.error("Could not save config settings.", e);
        }

        response.sendRedirect(PublickConstants.RECAPTCHA_CONFIG_PATH + ".html");
    }
}