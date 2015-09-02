package com.nateyolles.sling.publick.sightly;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Iterator;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.security.AccessControlManager;
import javax.script.Bindings;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.LinkRewriterService;

/**
 * The base class that all Sightly components should extend
 * from. Any sightly component extending this class should
 * simply override the #activate() method. Instead of using
 * Sling Bindings, the classes can use the provided helper
 * methods.
 */
public class WCMUse implements Use {

    /**
     * File extension for HTML files, used in getting page paths.
     */
    private static final String HTML_EXTENSION = ".html";

    /**
     * Logger instance to log and debug errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WCMUse.class);

    /**
     * Global bindings populated by the init method.
     */
    private Bindings bindings;

    /**
     * Sightly component initialization.
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        this.bindings = bindings;
        activate();
    }

    /**
     * The activate method is meant to be overridden as it's the
     * entry point to the extended class.
     */
    public void activate() {
    }

    /**
     * Get the current resource.
     *
     * @return The current resource.
     */
    public Resource getResource() {
        return (Resource)bindings.get(SlingBindings.RESOURCE);
    }

    /**
     * Get the resource resolver backed by the current resource.
     *
     * @return The current resource resolver.
     */
    public ResourceResolver getResourceResolver() {
        return getResource().getResourceResolver();
    }

    /**
     * Get the current resource properties.
     *
     * @return The current resource properties.
     */
    public ValueMap getProperties() {
        return getResource().adaptTo(ValueMap.class);
    }

    /**
     * Get the current Sling Script Helper.
     *
     * @return The current Sling Script Helper.
     */
    public SlingScriptHelper getSlingScriptHelper() {
        return (SlingScriptHelper)bindings.get(SlingBindings.SLING);
    }

    /**
     * Get the current request.
     *
     * @return The current Sling HTTP Servlet Request.
     */
    public SlingHttpServletRequest getRequest() {
        return (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
    }

    /**
     * Get the current response.
     *
     * @return The current Sling HTTP Servlet Response.
     */
    public SlingHttpServletResponse getResponse() {
        return (SlingHttpServletResponse)bindings.get(SlingBindings.RESPONSE);
    }

    /**
     * Get the current JCR session.
     *
     * @return The current JCR session.
     */
    public Session getSession() {
        return getResourceResolver().adaptTo(Session.class);
    }

    /**
     * Get the authorable status of the current user.
     *
     * @return true if the current user is an admin or author.
     */
    public boolean isAuthorable() {
        boolean authorable = false;

        JackrabbitSession js = (JackrabbitSession)getSession();

        try {
            Group authors = (Group)js.getUserManager().getAuthorizable(PublickConstants.GROUP_ID_AUTHORS);
            User user = (User)js.getUserManager().getAuthorizable(js.getUserID());

            authorable = user.isAdmin() || authors.isMember(user);
        } catch (RepositoryException e) {
            LOGGER.error("Could not determine group membership", e);
        }

        return authorable;
    }

    /**
     * Get the externalized relative path.
     *
     * @return The externalized relative path.
     */
    public String getRelativeExternalPath() {
        LinkRewriterService linkRewriter = getSlingScriptHelper().getService(LinkRewriterService.class);

        return linkRewriter != null
                ? linkRewriter.rewriteLink(getResource().getPath() + HTML_EXTENSION, getRequest().getServerName())
                : null;
    }

    /**
     * Get the externalized absolute path.
     *
     * @return The externalized absolute path.
     */
    public String getAbsoluteExternalPath() {
        return getAbsolutePath(getRelativeExternalPath());
    }

    /**
     * Generate the absolute resource path from the relative path.
     *
     * @return The absolute blog post display path.
     */
    public String getAbsolutePath(final String relativePath) {
        String displayPath = null;
        String newRelativePath = relativePath;

        if (StringUtils.isNotBlank(newRelativePath)) {
            try {
                URI uri = new URI(getRequest().getRequestURL().toString());

                if (relativePath.startsWith("/content/")) {
                    newRelativePath = StringUtils.removeStart(newRelativePath, "/content");
                }

                newRelativePath = StringUtils.removeEnd(newRelativePath, "/");

                displayPath = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(),
                        uri.getPort(), newRelativePath, uri.getQuery(), uri.getFragment()).toString();
            } catch (URISyntaxException e) {
                LOGGER.error("Could not get create absolute path from Request URL", e);
            }
        }

        return displayPath;
    }
}