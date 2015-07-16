package com.nateyolles.sling.publick.services.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;

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

    public boolean getEnabled() {
        return getProperty("enabled", Boolean.class);
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