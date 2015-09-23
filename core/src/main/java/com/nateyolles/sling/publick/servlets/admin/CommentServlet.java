package com.nateyolles.sling.publick.servlets.admin;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.CommentService;
import com.nateyolles.sling.publick.services.EmailService;
import com.nateyolles.sling.publick.services.LinkRewriterService;
import com.nateyolles.sling.publick.services.UserService;
import com.nateyolles.sling.publick.servlets.AdminServlet;

import org.apache.commons.lang.CharEncoding;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.Session;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for getting and manipulating comments. Administrative functions
 * include deleting comments, marking comments as spam and marking comments
 * as ham (valid). A get request will pull all comments. Post requests will
 * perform functions on an individual comment.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/comment")
public class CommentServlet extends AdminServlet {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServlet.class);

    /** Service to get, update, and delete comments. */
    @Reference
    private CommentService commentService;

    /** Service to determine if the current user has write permissions. */
    @Reference
    private UserService userService;

    /** Service to rewrite links taking extensionless URLs into account. */
    @Reference
    private LinkRewriterService linkRewriter;

    /** Delete comment action parameter value */
    private static final String ACTION_DELETE_COMMENT = "delete_comment";

    /** Spam comment action parameter value */
    private static final String ACTION_MARK_SPAM = "mark_spam";

    /** Ham comment action parameter value */
    private static final String ACTION_MARK_HAM = "mark_ham";

    /** Action request parameter */
    private static final String ACTION_PARAMETER = "action";

    /** Comment ID request parameter */
    private static final String COMMENT_ID_PARAMETER = "id";

    /** JSON response comment id key */
    private static final String JSON_ID = "id";

    /** JSON response number of comment replies key */
    private static final String JSON_REPLIES = "replies";

    /** JSON response is spam key */
    private static final String JSON_SPAM = "spam";

    /** JSON response comment's associated blog post key */
    private static final String JSON_POST = "post";

    /** JSON response comment's associated blog post title key */
    private static final String JSON_POST_TEXT = "text";

    /** JSON response comment's associated blog post URL key */
    private static final String JSON_POST_LINK = "link";

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
                json.put(JSON_ID, properties.get(JcrConstants.JCR_UUID, String.class));
                json.put(JSON_REPLIES, commentService.numberOfReplies(comment));
                json.put(JSON_SPAM, properties.get(PublickConstants.COMMENT_PROPERTY_SPAM, false));
                json.put(JSON_POST, new JSONObject()
                        .put(JSON_POST_TEXT, post.getValueMap().get(PublickConstants.COMMENT_PROPERTY_TITLE, String.class))
                        .put(JSON_POST_LINK, linkRewriter.rewriteLink(post.getPath(), request.getServerName())));

                jsonArray.put(json);
            }

            response.setStatus(SlingHttpServletResponse.SC_OK);
            writer.write(jsonArray.toString());
        } catch (JSONException e) {
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

        if (allowWrite) {
            final boolean result;
            final String action = request.getParameter(ACTION_PARAMETER);
            final String commentId = request.getParameter(COMMENT_ID_PARAMETER);

            if (ACTION_DELETE_COMMENT.equals(action)) {
                result = commentService.deleteComment(request, commentId);
            } else {
                result = false;
            }

            if (result) {
                response.setStatus(SlingHttpServletResponse.SC_OK);
                sendResponse(writer, "OK", "Comment successfully deleted.");
            } else {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendResponse(writer, "Error", "Comment could not be deleted.");
            }
        } else {
            response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
            sendResponse(writer, "Error", "Current user not authorized.");
        }
    }
}