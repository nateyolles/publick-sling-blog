package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.FileUploadService;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.resource.JcrResourceUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.*;
import javax.jcr.Session;
import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(paths = "/bin/recaptchaconfig")
public class RecaptchaConfigServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaConfigServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        final String siteKey = request.getParameter("siteKey");
        final String secretKey = request.getParameter("secretKey");
        final boolean enabled = Boolean.parseBoolean(request.getParameter("enabled"));
        final Map<String, Object> newProperties = new HashMap<String, Object>(){{
            put("siteKey", siteKey);
            put("secretKey", secretKey);
            put("enabled", enabled);
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
        
        response.sendRedirect(PublickConstants.ADMIN_CONFIG_PATH + ".html");
    }
}