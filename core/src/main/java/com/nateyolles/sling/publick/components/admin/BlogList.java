package com.nateyolles.sling.publick.components.admin;

import javax.jcr.NodeIterator;

import org.apache.sling.api.scripting.SlingScriptHelper;

import com.nateyolles.sling.publick.services.BlogService;
import com.nateyolles.sling.publick.sightly.WCMUse;

/**
 * Sightly component to list blog posts in the admin section.
 */
public class BlogList extends WCMUse {

    /**
     * Sling Script Helper to get services.
     */
    private SlingScriptHelper scriptHelper;

    /**
     * Blog Service to get plog posts.
     */
    private BlogService blogService;

    /**
     * Initialize Sightly component.
     */
    @Override
    public void activate() {
        scriptHelper = getSlingScriptHelper();
        blogService = scriptHelper.getService(BlogService.class);
    }

    /**
     * Get all blog posts without pagination.
     *
     * @return The blog posts ordered from newest to oldest.
     */
    public NodeIterator getBlogs() {
        NodeIterator nodes = null;

        if (blogService != null) {
            nodes = blogService.getPosts();
        }

        return nodes;
    }
}