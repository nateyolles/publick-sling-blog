package com.nateyolles.sling.publick.components.foundation;

import javax.jcr.NodeIterator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.BlogService;
import com.nateyolles.sling.publick.sightly.WCMUse;

/**
 * Sightly component to display a list of blog posts for the public visitor.
 * The component uses the Blog Service to get the posts. Pagination is
 * handled via the pagination component. This component reads the pagination
 * from querystring.
 */
public class BlogList extends WCMUse {

    /**
     * The querystring parameter for pagination.
     */
    private static final String PAGINATION_PARAMETER = "page";

    /**
     * Default blog posts per pagination page.
     */
    private static final long DEFAULT_POSTS_PER_PAGE = 5;

    /**
     * The page size property.
     */
    private static final String PAGE_SIZE_PROPERTY = "pageSize";

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BlogList.class);

    /**
     * The resource.
     */
    private static Resource resource;

    /**
     * The Sling Script Helper to get services.
     */
    private SlingScriptHelper scriptHelper;

    /**
     * The Blog Service to get posts.
     */
    private BlogService blogService;

    /**
     * The Sling HTTP Servlet Request.
     */
    private SlingHttpServletRequest request;

    /**
     * Blog posts per pagination page.
     */
    private long postsPerPage;

    /**
     * Sightly component initialization.
     */
    @Override
    public void activate() {
        resource = getResource();
        request = getRequest();
        scriptHelper = getSlingScriptHelper();
        blogService = scriptHelper.getService(BlogService.class);

        postsPerPage = resource.adaptTo(ValueMap.class).get(PAGE_SIZE_PROPERTY, DEFAULT_POSTS_PER_PAGE);
    }

    /**
     * Get blog posts from Blog Service using pagination offset
     * and number of posts.
     *
     * @return The blog posts.
     */
    public NodeIterator getBlogs() {
        NodeIterator nodes = null;
        final Long offset = getOffset();

        if (blogService != null) {
            nodes = blogService.getPosts(offset, postsPerPage);
        }

        return nodes;
    }

    /**
     * Get the starting point for pagination based on the querystring.
     *
     * If blog post to start at is determined by the page number from
     * the suffix multiplied by the number of posts per page. If the
     * page number is 1 or 0, start from the beginning.
     *
     * @return The blog post number to start at.
     */
    private Long getOffset() {
        Long offset = 0L;

        String param = request.getParameter(PAGINATION_PARAMETER);

        if (param != null) {
            try {
                offset = Long.valueOf(param);
            } catch (NumberFormatException e) {
                LOGGER.error("Could not get offset", e);
            }
        }

        if (offset <= 1) {
            return 0L;
        } else {
            return (offset - 1) * postsPerPage;
        }
    }
}