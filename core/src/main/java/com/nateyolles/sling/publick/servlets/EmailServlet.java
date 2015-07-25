package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.RecaptchaService;

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


@SlingServlet(paths = "/bin/sendmail")
public class EmailServlet extends SlingAllMethodsServlet {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServlet.class);

    @Reference
    private RecaptchaService recaptchaService;

    private String getIPAddress(SlingHttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");

        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        recaptchaService.validate(request, getIPAddress(request));
    }
}