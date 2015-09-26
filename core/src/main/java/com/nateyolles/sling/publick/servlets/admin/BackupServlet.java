package com.nateyolles.sling.publick.servlets.admin;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.PackageService;
import com.nateyolles.sling.publick.services.UserService;
import com.nateyolles.sling.publick.servlets.AdminServlet;

import org.apache.commons.lang.CharEncoding;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for getting, creating, uploading, installing and deleting
 * backup packages. A get request will provide a list of all packages.
 * Post requests will perform functions on an individual package.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/backup")
public class BackupServlet extends AdminServlet {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BackupServlet.class);

    /** Service to determine if the current user has write permissions. */
    @Reference
    private UserService userService;

    /** Service to get, install, upload, create and delete packages. */
    @Reference
    private PackageService packageService;

    /** Display format for the package creation date */
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /**
     * Return all packages on a GET request in order of newest to oldest.
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        final PrintWriter writer = response.getWriter();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/json");

        List<JcrPackage> packages = packageService.getPackageList(request);

        try {
            JSONArray jsonArray = new JSONArray();

            for (JcrPackage jcrPackage : packages) {
                final JSONObject json = new JSONObject();

                json.put("size", getSize(jcrPackage.getPackage().getSize()));
                json.put("date", dateFormat.format(jcrPackage.getPackage().getCreated().getTime()));
                json.put("name", jcrPackage.getDefinition().getId().getName());
                json.put("path", jcrPackage.getNode().getPath());
                json.put("id", jcrPackage.getDefinition().getId().toString());

                jsonArray.put(json);
            }

            response.setStatus(SlingHttpServletResponse.SC_OK);
            writer.write(jsonArray.toString());
        } catch (JSONException | RepositoryException e) {
            LOGGER.error("Could not write JSON", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Convert package bytes to kilobytes.
     *
     * @param size Size in bytes; -1 if not found.
     * @return Size in kilobytes with extension
     */
    private String getSize(final long size) {
        String value;

        if (size == -1) {
            value = "N/A";
        } else if (size <= 1000) {
            value = "1kb";
        } else {
            value = size / 1000 + "kb";
        }

        return value;
    }
}