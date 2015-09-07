package com.nateyolles.sling.publick.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.OsgiConfigurationService;
import com.nateyolles.sling.publick.services.RecaptchaService;

import org.osgi.framework.Constants;

/**
 * reCAPTCHA service to get keys and communicate with Google
 * to validate reCAPTCHA authentication. 
 */
@Service(value = RecaptchaService.class)
@Component(metatype = true,
           immediate = true,
           name = "Publick reCAPTCHA settings",
           description = "reCAPTCHA settings for Google's service.")
@Properties({
    @Property(name = RecaptchaServiceImpl.RECAPTCHA_SITE_KEY,
              label = "Site Key",
              description = "The public key used in the HTML."),
    @Property(name = RecaptchaServiceImpl.RECAPTCHA_SECRET_KEY,
              label = "Secret Key",
              description = "The secret key used for communication between your site and Google."),
    @Property(name = RecaptchaServiceImpl.RECAPTCHA_ENABLED,
              boolValue = RecaptchaServiceImpl.ENABLED_DEFAULT_VALUE,
              label = "Enabled",
              description = "Enable reCAPTCHA."),
    @Property(name = Constants.SERVICE_DESCRIPTION,
              value = "reCAPTCHA settings for Google's service."),
    @Property(name = Constants.SERVICE_VENDOR,
              value = "Publick")
})
public class RecaptchaServiceImpl implements RecaptchaService {

    /** Service to get and set OSGi properties. */
    @Reference
    private OsgiConfigurationService osgiService;

    /** PID of the current OSGi component */
    private static final String COMPONENT_PID = "Publick reCAPTCHA settings";

    /** Default value for enabled */
    public static final boolean ENABLED_DEFAULT_VALUE = false;

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaServiceImpl.class);

    /** reCAPTCHA service URL */
    private static final String RECAPTCHA_SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";

    /** Querystring parameters to submit to reCAPTCHA service */
    private static final String RECAPTCHA_SERVICE_QUERYSTRING = "?secret=%s&response=%s&remoteip=%s";

    /** reCAPTCHA JSON key that identifies validation */
    private static final String RECAPTCHA_SUCCESS = "success";

    /** The reCAPTCHA request parameter */
    private static final String RECAPTCHA_REQUEST_PARAMETER = "g-recaptcha-response";

    /**
     * Set multiple properties for the reCAPTCHA Settings service.
     *
     * This is useful for setting multiple properties as the same
     * time in that the OSGi component will only be updated once
     * and thus reset only once.
     *
     * @param properties A map of properties to set.
     * @return true if save was successful.
     */
    public boolean setProperties(final Map<String, Object> properties) {
        return osgiService.setProperties(COMPONENT_PID, properties);
    }

    /**
     * Get the reCAPTCHA site key.
     *
     * @return The reCAPTCHA site key.
     */
    public String getSiteKey() {
        return osgiService.getStringProperty(COMPONENT_PID, RECAPTCHA_SITE_KEY, null);
    }

    /**
     * Set the public reCAPTCHA site key.
     *
     * @param sitekey The public reCAPTCHA site key.
     * @return true if the save was successful.
     */
    public boolean setSiteKey(final String siteKey) {
        return osgiService.setProperty(COMPONENT_PID, RECAPTCHA_SITE_KEY, siteKey);
    }

    /**
     * Get the reCAPTCHA secret key.
     *
     * @return The reCAPTCHA secret key.
     */
    public String getSecretKey() {
        return osgiService.getStringProperty(COMPONENT_PID, RECAPTCHA_SECRET_KEY, null);
    }

    /**
     * Set the private reCAPTCHA secret key.
     *
     * @param secretkey The private reCAPTCHA secret key.
     * @return true if the save was successful.
     */
    public boolean setSecretKey(final String secretKey) {
        return osgiService.setProperty(COMPONENT_PID, RECAPTCHA_SECRET_KEY, secretKey);
    }

    /**
     * Is the reCAPTCHA service enabled.
     *
     * @return True if the reCAPTCHA service is enabled.
     */
    public boolean getEnabled() {
        return osgiService.getBooleanProperty(COMPONENT_PID, RECAPTCHA_ENABLED, ENABLED_DEFAULT_VALUE);
    }

    /**
     * Set whether the reCAPTCHA service is enabled.
     *
     * @param enabled The enabled property to set.
     * @return true if the save was successful.
     */
    public boolean setEnabled(final boolean enabled) {
        return osgiService.setProperty(COMPONENT_PID, RECAPTCHA_ENABLED, enabled);
    }

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param request The SlingHttpServletRequest with the reCAPTCHA parameter
     *                  from the client-side validation.
     * @param remoteIP The remote user's IP address.
     * @return true if not a robot
     */
    public boolean validate(final SlingHttpServletRequest request) {
        return validate(request.getParameter(RECAPTCHA_REQUEST_PARAMETER), getIPAddress(request));
    }

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param recaptchaResponse The reCAPTCHA parameter from the SlingHttpServletRequest
     *          and the client-side validation.
     * @param remoteIP The remote user's IP address.
     * @return true if not a robot
     */
    public boolean validate(final String recaptchaResponse, final String remoteIP) {
        final String charset = StandardCharsets.UTF_8.name();
        final String secretKey = getSecretKey();

        URLConnection connection = null;
        InputStream is = null;

        boolean validated = false;

        if (getEnabled() && StringUtils.isNotEmpty(secretKey)
                && StringUtils.isNotEmpty(recaptchaResponse)
                && StringUtils.isNotEmpty(remoteIP)) {

            try {
                String query = String.format(RECAPTCHA_SERVICE_QUERYSTRING,
                    URLEncoder.encode(secretKey, charset),
                    URLEncoder.encode(recaptchaResponse, charset),
                    URLEncoder.encode(remoteIP, charset));

                connection = new URL(RECAPTCHA_SERVICE_URL + query).openConnection();
                is = connection.getInputStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(is, writer, charset);
                String responseString = writer.toString();
                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.getBoolean(RECAPTCHA_SUCCESS)) {
                    validated = true;
                }
            } catch (IOException e) {
                LOGGER.error("Could not validate recaptcha.", e);
            } catch (JSONException e) {
                LOGGER.error("Could not create JSON.", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOGGER.error("Could not close Input Stream.", e);
                    }
                }
            }
        }

        return validated;
    }

    /**
     * Service activation.
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
    }

    /**
     * Service Deactivation.
     *
     * @param ctx The current component context.
     */
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
    }

    /**
     * Get the submitter's IP address.
     *
     * @param request The SlingHttpServlet request.
     * @return The submitter's IP address.
     */
    private String getIPAddress(final SlingHttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");

        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }
}