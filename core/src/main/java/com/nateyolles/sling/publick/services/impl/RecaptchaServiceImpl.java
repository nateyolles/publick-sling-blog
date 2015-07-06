package com.nateyolles.sling.publick.services.impl;

import java.io.InputStream;
import java.util.Map;

import javax.jcr.Node;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.resource.JcrResourceUtil;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.RecaptchaService;

@Service( value = RecaptchaService.class )
@Component( metatype = true, immediate = true )
public class RecaptchaServiceImpl implements RecaptchaService {

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaServiceImpl.class);

  public String getSiteKey() {
      return getProperty("siteKey", String.class);
  }

  public String getSecretKey() {
      return getProperty("secretKey", String.class);
  }

  public boolean getEnabled() {
      return getProperty("enabled", Boolean.class);
  }

  @Activate
  protected void activate(Map<String, Object> properties) {
  }

  @Deactivate
  protected void deactivate(ComponentContext ctx) {
  }

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