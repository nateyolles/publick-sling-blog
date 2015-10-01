package com.nateyolles.sling.publick.servlets.admin;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.FileUploadService;
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
import javax.jcr.Session;
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

    /** File Upload service to handle the upload. */
    @Reference
    private FileUploadService fileUploadService;

    /** Service to determine if the current user has write permissions. */
    @Reference
    private UserService userService;

    /** Service to get, install, upload, create and delete packages. */
    @Reference
    private PackageService packageService;

    /** Display format for the package creation date */
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /** Action request parameter */
    private static final String ACTION_PARAMETER = "action";

    /** Create new package action parameter value */
    private static final String ACTION_CREATE = "create_package";

    /** Install new package action parameter value */
    private static final String ACTION_INSTALL = "install_package";

    /** Install new package action parameter value */
    private static final String ACTION_UPLOAD = "upload_package";

    /** Delete package action parameter value */
    private static final String ACTION_DELETE = "delete_package";

    /** Package name request parameter */
    private static final String PACKAGE_NAME_PARAMETER = "name";

    /** JSON response size key */
    private static final String JSON_SIZE_PROPERTY = "size";

    /** JSON response date key */
    private static final String JSON_DATE_PROPERTY = "date";

    /** JSON response name key */
    private static final String JSON_NAME_PROPERTY = "name";

    /** JSON response path key */
    private static final String JSON_PATH_PROPERTY = "path";

    /** JSON response id key */
    private static final String JSON_ID_PROPERTY = "id";

    /**
     * Return all packages on a GET request in order of newest to oldest.
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        final PrintWriter writer = response.getWriter();

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/json");

        List<JcrPackage> packages = packageService.getPackageList(request);

        try {
            JSONArray jsonArray = new JSONArray();

            for (JcrPackage jcrPackage : packages) {
                final JSONObject json = getJsonFromJcrPackage(jcrPackage);

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
     * Handle post operations based on the "action" request parameter
     * for deletion, update, spam and ham updates.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        final PrintWriter writer = response.getWriter();
        final boolean allowWrite = userService.isAuthorable(request.getResourceResolver().adaptTo(Session.class));

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/json");

        int status = SlingHttpServletResponse.SC_FORBIDDEN;
        String header = "ERROR";
        String message = "Current user not authorized.";
        String data = null;

        if (allowWrite) {
            final String action = request.getParameter(ACTION_PARAMETER);
            final String packageName = request.getParameter(PACKAGE_NAME_PARAMETER);

            if (ACTION_CREATE.equals(action)) {
                final JcrPackage savedPackage = packageService.createBackupPackage(request, packageName);

                if (savedPackage != null) {
                    try {
                        status = SlingHttpServletResponse.SC_OK;
                        header = "OK";
                        message = "Package successfully created.";
                        data = getJsonFromJcrPackage(savedPackage).toString();
                    } catch (JSONException | RepositoryException e) {
                        status = SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                        header = "Error";
                        message = "JSON response could not be created";
                    }
                } else {
                    status = SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    header = "Error";
                    message = "Package could not be created.";
                }
            } else if (ACTION_INSTALL.equals(action)) {
                final boolean result = packageService.installBackupPackage(request, packageName);

                if (result) {
                    status = SlingHttpServletResponse.SC_OK;
                    header = "OK";
                    message = "Package successfully installed.";
                } else {
                    status = SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    header = "Error";
                    message = "Package could not be installed.";
                }
            } else if (ACTION_UPLOAD.equals(action)){
                final JcrPackage savedPackage = packageService.uploadBackupPackage(request);

                if (savedPackage != null) {
                    try {
                        status = SlingHttpServletResponse.SC_OK;
                        header = "OK";
                        message = "Package successfully uploaded.";
                        data = getJsonFromJcrPackage(savedPackage).toString();
                    } catch (JSONException | RepositoryException e) {
                        status = SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                        header = "Error";
                        message = "JSON response could not be created";
                    }
                } else {
                    status = SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    header = "Error";
                    message = "Package could not be uploaded.";
                }
            } else if (ACTION_DELETE.equals(action)){
                final boolean result = packageService.deleteBackupPackage(request, packageName);

                if (result) {
                    status = SlingHttpServletResponse.SC_OK;
                    header = "OK";
                    message = "Package successfully deleted.";
                } else {
                    status = SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    header = "Error";
                    message = "Package could not be deleted.";
                }
            } else {
                status = SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                header = "Error";
                message = "Action could not be performed.";
            }
        }

        response.setStatus(status);
        sendResponse(writer, header, message, data);
    }

    /**
     * Get a JSONObject from the JCR package configured for the Angular model.
     *
     * @param jcrPackage The JCR Package to retrieve data from.
     * @return the JSON Object configured for the Angular model.
     * @throws JSONException
     * @throws RepositoryException
     * @throws IOException
     */
    private JSONObject getJsonFromJcrPackage(final JcrPackage jcrPackage)
            throws JSONException, RepositoryException, IOException {

        final JSONObject json = new JSONObject();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        json.put(JSON_SIZE_PROPERTY, getSize(jcrPackage.getPackage().getSize()));
        json.put(JSON_DATE_PROPERTY, dateFormat.format(jcrPackage.getPackage().getCreated().getTime()));
        json.put(JSON_NAME_PROPERTY, jcrPackage.getDefinition().getId().getName());
        json.put(JSON_PATH_PROPERTY, jcrPackage.getNode().getPath());
        json.put(JSON_ID_PROPERTY, jcrPackage.getDefinition().getId().toString());

        return json;
    }

    /**
     * Convert package size to readable format.
     *
     * TODO: Decide this future. Why are sizes always -1?
     *
     * @param size Size in bytes; -1 if not found.
     * @return Size in kilobytes with extension
     */
    private String getSize(final long size) {
        String value;

        if (size == -1) {
            value = "N/A";
        } else if (size <= 1024) {
            value = "1kb";
        } else if (size <= 1048576) {
            value = size / 1024 + "kb";
        } else {
            value = size / 1048576 + "mb";
        }

        return value;
    }
}