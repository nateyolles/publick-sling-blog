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
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.RecaptchaService;

/**
 * reCAPTCHA service to get keys and communicate with Google
 * to validate reCAPTCHA authentication. 
 */
@Service( value = RecaptchaService.class )
@Component( metatype = true, immediate = true )
public class RecaptchaServiceImpl implements RecaptchaService {

    /**
     * ResourceResolver factory to get a ResourceRolver.
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaServiceImpl.class);

    /**
     * reCAPTCHA service URL.
     */
    private static final String RECAPTCHA_SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";

    /**
     * Querystring parameters to submit to reCAPTCHA service.
     */
    private static final String RECAPTCHA_SERVICE_QUERYSTRING = "?secret=%s&response=%s&remoteip=%s";

    /**
     * reCAPTCHA JSON key that identifies validation.
     */
    private static final String RECAPTCHA_SUCCESS = "success";

    /**
     * The reCAPTCHA request parameter.
     */
    private static final String RECAPTCHA_REQUEST_PARAMETER = "g-recaptcha-response";

    /**
     * Get the reCAPTCHA site key.
     *
     * @return The reCAPTCHA site key.
     */
    public String getSiteKey() {
        return getProperty("siteKey", String.class);
    }

    /**
     * Get the reCAPTCHA secret key.
     *
     * @return The reCAPTCHA secret key.
     */
    public String getSecretKey() {
        return getProperty("secretKey", String.class);
    }

    /**
     * Is the reCAPTCHA service enabled.
     *
     * @return True if the reCAPTCHA service is enabled.
     */
    public boolean getEnabled() {
        return getProperty("enabled", Boolean.class);
    }

    /**
     * Validate reCAPTCHA with the secret key and Google's service.
     *
     * @param request The SlingHttpServletRequest with the reCAPTCHA parameter
     *                  from the client-side validation.
     * @param remoteIP The remote user's IP address.
     * @return true if not a robot
     */
    public boolean validate(SlingHttpServletRequest request, String remoteIP) {
        return validate(request.getParameter(RECAPTCHA_REQUEST_PARAMETER), remoteIP);
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

        if (StringUtils.isNotEmpty(secretKey) && StringUtils.isNotEmpty(recaptchaResponse) && StringUtils.isNotEmpty(remoteIP)) {
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
     * Get the specified reCAPTCHA config property.
     *
     * @param propertyName The property name to get.
     * @param type The property class type.
     * @return The property value.
     */
    private <T> T getProperty(String propertyName, Class<T> type) {
        T property = null;
        ResourceResolver resolver = null;

        try {
            resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);

            if (resolver != null) {
                Resource recaptcha = resolver.getResource(PublickConstants.CONFIG_RECAPTCHA_PATH);

                if (recaptcha != null) {
                    ValueMap properties = recaptcha.adaptTo(ValueMap.class);
                    property = properties.get(propertyName, type);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Could not get Resource Resolver", e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
                resolver = null;
            }
        }

        return property;
    }
}