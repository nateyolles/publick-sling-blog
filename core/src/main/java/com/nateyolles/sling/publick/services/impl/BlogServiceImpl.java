package com.nateyolles.sling.publick.services.impl;

import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.BlogService;

@Service( value = BlogService.class )
@Component( metatype = true, immediate = true )
public class BlogServiceImpl implements BlogService {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BlogServiceImpl.class);

    /**
     * JCR_SQL2 query to get all blog posts in order of newest first.
     */
    private static final String BLOG_QUERY = String.format("SELECT * FROM [%s] AS s WHERE "
            + "ISDESCENDANTNODE([%s]) AND s.[%s] = '%s' ORDER BY [%s] desc",
            PublickConstants.NODE_TYPE_PAGE,
            PublickConstants.BLOG_PATH,
            JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY,
            PublickConstants.PAGE_TYPE_BLOG,
            JcrConstants.JCR_CREATED);

    /**
     * The JCR session.
     */
    private Session session;

    /**
     * The JCR Repository.
     */
    @Reference
    private SlingRepository repository;

    /**
     * Get all blog posts without pagination.
     *
     * @return The blog posts.
     */
    public NodeIterator getPosts() {
        return getPosts(null, null);
    }

    /**
     * Get blog posts with pagination
     *
     * @param offset The starting point of blog posts to return.
     * @param limit The number of blog posts to return.
     * @return The blog posts.
     */
    public NodeIterator getPosts(Long offset, Long limit) {
        NodeIterator nodes = null;

        if (session != null) {
            try {
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                Query query = queryManager.createQuery(BLOG_QUERY, Query.JCR_SQL2);

                if (offset != null) {
                    query.setOffset(offset);
                }

                if (limit != null) {
                    query.setLimit(limit);
                }

                QueryResult result = query.execute();
                nodes = result.getNodes();
            } catch (RepositoryException e) {
                LOGGER.error("Could not search repository", e);
            }
        }

        return nodes;
    }

    /**
     * Get the number of pagination pages based on number of blog
     * posts found and specified number of blog posts per page.
     *
     * @param pageSize The number of blog posts per pagination page.
     * @return The number of pagination pages.
     */
    public long getNumberOfPages(int pageSize) {
        long posts = getNumberOfPosts();

        return (long)Math.ceil((double)posts / pageSize);
    }

    /**
     * Get number of blog posts.
     *
     * @return The number of blog posts.
     */
    public long getNumberOfPosts() {
        return getPosts().getSize();
    }

    /**
     * Activate Service.
     *
     * @param properties
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
        try {
            session = repository.loginAdministrative(null);
        } catch (LoginException e) {
            LOGGER.error("Could not get session.", e);
        } catch (RepositoryException e) {
            LOGGER.error("Could not get session.", e);
        }
    }

    /**
     * Deactivate Service.
     *
     * @param ctx
     */
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        if (session != null && session.isLive()) {
            session.logout();
            session = null;
        }
    }
}