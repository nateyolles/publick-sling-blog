package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
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

@SlingServlet(paths = "/bin/uploadfile")
public class UploadFileServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        final String path = request.getParameter("path");
        final String image = saveImage(request);

        response.getWriter().print("Success");
        response.setStatus(200);
    }

    /**
     * Save uploaded image to the JCR under the default images node.
     *
     * @param request   the current SlingHTTPServletRequest
     * @return          the image path, null if the image wasn't saved
     */
    private String saveImage(SlingHttpServletRequest request) {
        final RequestParameterMap params = request.getRequestParameterMap();
        ResourceResolver resolver = request.getResourceResolver();

        String imagePath = null;

        for (final Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
            final String key = pairs.getKey();
            final RequestParameter[] pArr = pairs.getValue();
            final RequestParameter param = pArr[0];

            if (!param.isFormField()) {
                final String name = param.getFileName();
                final String mimeType = param.getContentType();

                try {
                    final InputStream stream = param.getInputStream();
                    Resource imagesParent = resolver.getResource(PublickConstants.IMAGE_PATH);
                    Node imageNode = JcrUtils.putFile(imagesParent.adaptTo(Node.class), name, mimeType, stream);
                    resolver.commit();

                    imagePath = imageNode.getPath();
                } catch (javax.jcr.RepositoryException e) {
                    LOGGER.error("Could not save image to repository.", e);
                } catch (java.io.IOException e) {
                    LOGGER.error("Could not get image input stream", e);
                } finally {
                    /* Exit loop after file is found. */
                    break;
                }
            }
        }

        return imagePath;
    }
}