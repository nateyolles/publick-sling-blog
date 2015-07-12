package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.FileUploadService;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.jcr.resource.JcrResourceUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.*;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(paths = "/bin/editblogpost")
public class EditBlogPostServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditBlogPostServlet.class);
    private static final String BLOG_PATH = "/%d/%02d";
    private static final String BLOG_ROOT = "blog";

    @Reference
    private FileUploadService fileUploadService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        final String title = request.getParameter("title");
        final String description = request.getParameter("description");
        final String content = request.getParameter("content");
        final String url = request.getParameter("url");
        final boolean visible = Boolean.parseBoolean(request.getParameter("visible"));
        final String[] keywords = request.getParameterValues("keywords");
        final long month = Long.parseLong(request.getParameter("month"));
        final long year = Long.parseLong(request.getParameter("year"));
        final String path = String.format(BLOG_PATH, year, month);
        final String blogPath = PublickConstants.BLOG_PATH + path + "/" + url;
        String image = fileUploadService.uploadFile(request, PublickConstants.IMAGE_PATH);

        Resource existingNode = resolver.getResource(blogPath);

        if (image == null && existingNode != null) {
            image = existingNode.adaptTo(ValueMap.class).get("image", String.class);
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, PublickConstants.NODE_TYPE_PAGE);
        properties.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, PublickConstants.PAGE_TYPE_BLOG);
        properties.put("title", title);
        properties.put("visible", visible);
        properties.put("content", content);
        properties.put("description", description);
        properties.put("month", month);
        properties.put("year", year);

        if (image != null) {
            properties.put("image", image);
        }

        if (keywords != null) {
            properties.put("keywords", keywords);
        }

        try {
            UserManager userManager = ((JackrabbitSession)session).getUserManager();
            Authorizable auth = userManager.getAuthorizable(session.getUserID());
            properties.put("author", auth.getID());
        } catch (RepositoryException e) {
            LOGGER.error("Could not get user.", e);
        }

        try {

            if (existingNode != null) {
                ModifiableValueMap existingProperties = existingNode.adaptTo(ModifiableValueMap.class);
                existingProperties.putAll(properties);
            } else {
                Node node = JcrResourceUtil.createPath(resolver.getResource(PublickConstants.CONTENT_PATH).adaptTo(Node.class), BLOG_ROOT + path, NodeType.NT_UNSTRUCTURED, NodeType.NT_UNSTRUCTURED, true);

                resolver.create(resolver.getResource(node.getPath()), url, properties);
            }

            resolver.commit();
            resolver.close();

            response.sendRedirect(PublickConstants.ADMIN_LIST_PATH + ".html");
        } catch (RepositoryException e) {
            LOGGER.error("Could not save blog to repository.", e);
            response.sendRedirect(request.getHeader("referer"));
        } catch (PersistenceException e){
            LOGGER.error("Could not save blog to repository.", e);
            response.sendRedirect(request.getHeader("referer"));
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
                resolver = null;
            }
        }
    }
}