package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.SystemSettingsService;

import org.apache.felix.scr.annotations.Reference;
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
 * Post servlet to save system config updates.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/systemconfig")
public class SystemConfigServlet extends SlingAllMethodsServlet {

    @Reference
    SystemSettingsService systemSettingsService;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaConfigServlet.class);

    /**
     * The blog name property of the system config node.
     */
    private static final String BLOG_NAME_PROPERTY = "blogName";

    /**
     * Save system properties on POST.
     *
     * @param request The Sling HTTP servlet request.
     * @param response The Sling HTTP servlet response.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        final String blogName = request.getParameter(BLOG_NAME_PROPERTY);

        systemSettingsService.setBlogName(blogName);

        response.sendRedirect(PublickConstants.SYSTEM_CONFIG_PATH + ".html");
    }
}