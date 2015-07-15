package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.services.FileUploadService;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.servlet.ServletException;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to upload files. Uses the File Upload Service and sends
 * back either a 200 or 404 response.
 */
@SlingServlet(paths = "/bin/uploadfile")
public class FileUploadServlet extends SlingAllMethodsServlet {

    /**
     * File Upload service to handle the upload.
     */
    @Reference
    private FileUploadService fileUploadService;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadServlet.class);

    /**
     * The request parameter for the path to the parent resource of where
     * to save the file.
     */
    private static final String PATH_REQUEST_PARAM = "path";

    /**
     * Handle POST request and send appropriate response after file
     * is uploaded.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        final String path = request.getParameter(PATH_REQUEST_PARAM);
        String file = fileUploadService.uploadFile(request, path);

        if (file != null) {
            response.setStatus(response.SC_OK);
        } else {
            response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
        }
    }
}