package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.RecaptchaService;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.jcr.resource.JcrResourceUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add comment to blog post. Comments are stored under a different parent node as
 * blog posts for ease of access control. The comment node structure mirrors the
 * blog post node structure and this servlet will create it.
 *
 * Blog posts are stored under: /content/blogs/2015/01/title
 * Comments are stored under: /content/comments/2015/01/title/comment_1
 *
 * Comments can be nested two levels deep.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_PUBLIC + "/addcomment")
public class CommentServlet extends SlingAllMethodsServlet {

    /**
     * Resource Resolver Factory to get resource resolver in order
     * to get resources and write to the JCR.
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * reCAPTCHA service to verify user isn't a robot.
     */
    @Reference
    private RecaptchaService recaptchaService;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServlet.class);

    /**
     * Resource Resolver to get resources.
     */
    private ResourceResolver resolver;

    /**
     * Request parameter sent from the comment form for Author.
     */
    private static final String AUTHOR_PARAMETER = "author";

    /**
     * Request parameter sent from the comment form for Comment.
     */
    private static final String COMMENT_PARAMETER = "comment";

    /**
     * Request parameter sent from the comment form for the blog path.
     */
    private static final String BLOG_PATH_PARAMETER = "blogPath";

    /**
     * Request parameter sent from the comment form for the parent comment path.
     */
    private static final String COMMENT_PATH_PARAMETER = "commentPath";

    /**
     * Comment node name in the form of "comment_1", "comment_2", etc...
     */
    private static final String COMMENT_NODE_NAME = "comment_%d";

    /**
     * Save the comment to the JCR.
     *
     * Get or create the comment node structure to mirror the blog post node structure.
     * Create the comment node under the structure and save the author, comment, and date.
     * Currently redirects back to the same page. Verifies against the reCAPTCHA service.
     * The next version will be an asynchronous post.
     *
     * @param request The Sling HTTP servlet request.
     * @param response The Sling HTTP servlet response.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        final String blogPath = request.getParameter(BLOG_PATH_PARAMETER);

        if (!recaptchaService.getEnabled() || recaptchaService.validate(request)) {
            final String author = request.getParameter(AUTHOR_PARAMETER);
            final String comment = request.getParameter(COMMENT_PARAMETER);
            final String commentPath = request.getParameter(COMMENT_PATH_PARAMETER);

            try {
                resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);

                String parentPath = StringUtils.isNotBlank(commentPath)
                                    ? commentPath
                                    : blogPath.replace(PublickConstants.BLOG_PATH, PublickConstants.COMMENTS_PATH);

                JcrResourceUtil.createPath(parentPath, JcrResourceConstants.NT_SLING_ORDERED_FOLDER,
                        JcrResourceConstants.NT_SLING_ORDERED_FOLDER, resolver.adaptTo(Session.class), true);

                String nodeName = getCommentName(parentPath);

                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(AUTHOR_PARAMETER, author);
                properties.put(COMMENT_PARAMETER, comment);
                properties.put(JcrConstants.JCR_PRIMARYTYPE, PublickConstants.NODE_TYPE_COMMENT);

                Resource commentResource = resolver.create(resolver.getResource(parentPath), nodeName, properties);
                Node commentNode = commentResource.adaptTo(Node.class);
                commentNode.addMixin(NodeType.MIX_CREATED);

                resolver.commit();
            } catch (LoginException e) {
                LOGGER.error("Could not login", e);
            } catch (RepositoryException e) {
                LOGGER.error("Could not create comment node", e);
            } catch (Exception e){
                LOGGER.error("Could not create comment node", e);
            }
        }

        response.sendRedirect(blogPath + ".html");
    }

    /**
     * Get the first available node name for a comment. Comments are named
     * "comment_1", "comment_2", etc... There is a limit of 1000 comments
     * per parent node.
     *
     * @param parentPath The path of the parent node in the /content/comments
     *                   node structure
     * @return The first available node name.
     */
    private String getCommentName(final String parentPath) {
        final int MAX_TRIES = 1000;

        if (resolver.getResource(parentPath) != null) {
            for (int i=0; i < MAX_TRIES; i++) {
                String newName = String.format(COMMENT_NODE_NAME, i);
                if (resolver.getResource(parentPath + "/" + newName) == null) {
                    return newName;
                }
            }
        }

        return null;
    }
}