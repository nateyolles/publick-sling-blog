package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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

@SlingServlet(paths = "/bin/editblogpost")
public class EditBlogPostServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditBlogPostServlet.class);
    private static final String BLOG_PATH = "blog/%d/%02d";

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resolver = request.getResourceResolver();
        String blogPath = null;

        final String title = request.getParameter("title");
        final String content = request.getParameter("content");
        final String url = request.getParameter("url");
        final boolean visible = Boolean.parseBoolean(request.getParameter("visible"));
        final String[] keywords = request.getParameterValues("keywords");
        final long month = Long.parseLong(request.getParameter("month"));
        final long year = Long.parseLong(request.getParameter("year"));
        final String path = String.format(BLOG_PATH, year, month);
        final String image = saveImage(request);

        try {
            Node node = JcrResourceUtil.createPath(resolver.getResource(PublickConstants.CONTENT_PATH).adaptTo(Node.class), path, NodeType.NT_UNSTRUCTURED, NodeType.NT_UNSTRUCTURED, true);

            blogPath = node.getPath() + "/" + url;

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("jcr:primaryType", PublickConstants.NODE_TYPE_PAGE);
            properties.put("sling:resourceType", PublickConstants.PAGE_TYPE_BLOG);
            properties.put("title", title);
            properties.put("visible", visible);
            properties.put("content", content);
            properties.put("keywords", keywords);
            properties.put("month", month);
            properties.put("year", year);
            properties.put("image", image);

            resolver.create(resolver.getResource(node.getPath()), url, properties);

            resolver.commit();
            resolver.close();
        } catch (javax.jcr.RepositoryException e) {
            LOGGER.error("Could not save blog to repository.", e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
                resolver = null;
            }
        }

        response.getWriter().print("Success: " + blogPath);
        response.setStatus(200);
    }

    /**
     * Save uploaded image to the JCR under the /content/images node.
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