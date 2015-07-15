package com.nateyolles.sling.publick.components.admin;

import javax.jcr.NodeIterator;
import javax.script.Bindings;

import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.services.BlogService;

/**
 * Sightly component to list blog posts in the admin section.
 */
public class BlogList implements Use {

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
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);
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