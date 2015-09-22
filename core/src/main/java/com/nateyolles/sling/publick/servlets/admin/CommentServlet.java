package com.nateyolles.sling.publick.servlets.admin;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.CommentService;
import com.nateyolles.sling.publick.services.LinkRewriterService;

import org.apache.commons.lang.CharEncoding;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for getting and manipulating comments. Administrative functions
 * include deleting comments, marking comments as spam and marking comments
 * as ham (valid). A get request will pull all comments. Post requests will
 * perform functions on an individual comment.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/comment")
public class CommentServlet extends SlingAllMethodsServlet {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServlet.class);

    /** Service to get, update, and delete comments. */
    @Reference
    private CommentService commentService;

    /** Service to rewrite links taking extensionless URLs into account. */
    @Reference
    private LinkRewriterService linkRewriter;

    /**
     * Return all comments on a GET request in order of newest to oldest.
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        final PrintWriter writer = response.getWriter();

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/json");

        List<Resource> comments = commentService.getComments(request);

        try {
            JSONArray jsonArray = new JSONArray();

            for (Resource comment : comments) {
                final JSONObject json = new JSONObject();
                final ValueMap properties = comment.getValueMap();
                final Resource post = commentService.getParentPost(comment);

                json.put(PublickConstants.COMMENT_PROPERTY_COMMENT,
                        properties.get(PublickConstants.COMMENT_PROPERTY_COMMENT, String.class));
                json.put("replies", commentService.numberOfReplies(comment));
                json.put("spam", properties.get(PublickConstants.COMMENT_PROPERTY_SPAM, false));
                json.put("post", new JSONObject()
                        .put("text", post.getValueMap().get("title", String.class))
                        .put("link", linkRewriter.rewriteLink(post.getPath(), request.getServerName())));

                jsonArray.put(json);
            }

            response.setStatus(SlingHttpServletResponse.SC_OK);
            writer.write(jsonArray.toString());
        } catch (JSONException e) {
            LOGGER.error("Could not write JSON", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}