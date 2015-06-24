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

    @Reference
    private ResourceResolverFactory factory;

    private static final Logger LOGGER = LoggerFactory.getLogger(EditBlogPostServlet.class);
    private static final String BLOG_PATH = "blog/%s/%s";

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resolver = request.getResourceResolver();
        final String title = request.getParameter("title");
        final String content = request.getParameter("content");
        final String month = request.getParameter("month");
        final String year = request.getParameter("year");
        final String url = request.getParameter("url");
        final boolean visible = Boolean.parseBoolean(request.getParameter("visible"));
        final String[] keywords = request.getParameterValues("keywords");

        final String path = String.format(BLOG_PATH, year, month);

        try {
            Node node = JcrResourceUtil.createPath(resolver.getResource(PublickConstants.CONTENT_PATH).adaptTo(Node.class), path, NodeType.NT_UNSTRUCTURED, NodeType.NT_UNSTRUCTURED, true);

            resolver.create(resolver.getResource(node.getPath()), url, new HashMap<String, Object>(){{
                put("jcr:primaryType", PublickConstants.NODE_TYPE_PAGE);
                put("sling:resourceType", PublickConstants.PAGE_TYPE_BLOG);
                put("title", title);
                put("visible", visible);
                put("content", content);
                put("keywords", keywords);
            }});

            resolver.commit();
            resolver.close();
        } catch (javax.jcr.RepositoryException e) {
            LOGGER.error("Could not save blog to repository.", e);
        }

        final RequestParameterMap params = request.getRequestParameterMap();
        
        for (final Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
            final String key = pairs.getKey();
            final RequestParameter[] pArr = pairs.getValue();
            final RequestParameter param = pArr[0];

            if (!param.isFormField()) {
                final String name = param.getFileName();
                final String mimeType = param.getContentType();
                final InputStream stream = param.getInputStream();

                try {
                    
                    Resource resource = resolver.getResource(PublickConstants.ADMIN_PATH);
                    
                    JcrUtils.putFile(resource.adaptTo(Node.class), name, mimeType, stream);
    
                    resolver.commit();
                    resolver.close();
                } catch (javax.jcr.RepositoryException e) {
                    LOGGER.error("Could not save image to repository.", e);
                } finally {
                    if (resolver != null) {
                        resolver.close();
                        resolver = null;
                    }
                    break;
                }
            }
        }
        
        response.getWriter().print("date" + title + ": " + title);
        response.setStatus(200);
    }
}
