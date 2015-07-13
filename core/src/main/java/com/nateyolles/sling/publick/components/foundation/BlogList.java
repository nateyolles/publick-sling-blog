package com.nateyolles.sling.publick.components.foundation;

import javax.jcr.NodeIterator;
import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.BlogService;

public class BlogList implements Use {

    private static final long POSTS_PER_PAGE = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogList.class);

    private SlingScriptHelper scriptHelper;
    private BlogService blogService;
    private SlingHttpServletRequest request;

    @Override
    public void init(Bindings bindings) {
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
        scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);
        blogService = scriptHelper.getService(BlogService.class);
    }

    public NodeIterator getBlogs() {
        NodeIterator nodes = null;
        final Long offset = getOffset();

        if (blogService != null) {
            nodes = blogService.getPosts(offset, POSTS_PER_PAGE);
        }

        return nodes;
    }

    private Long getOffset() {
        Long offset = 0L;

        String suffix = request.getRequestPathInfo().getSuffix();

        if (suffix != null) {
            if (suffix.startsWith("/")) {
                suffix = suffix.substring(1);
            }

            try {
                offset = Long.valueOf(suffix);
            } catch (NumberFormatException e) {
                LOGGER.error("Could not get offset", e);
            }
        }

        if (offset <= 1) {
            return 0L;
        } else {
            return (offset - 1) * POSTS_PER_PAGE;
        }
    }
}