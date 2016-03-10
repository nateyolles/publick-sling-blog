package com.nateyolles.sling.publick.components.foundation;

import java.util.Iterator;
import javax.jcr.query.Query;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.sightly.WCMUse;

/**
 * Backing for Sightly Collapsible Blog List widget which shows all blog posts
 * organized into a collapsible, accordian-like view. This bean returns an
 * Iterator of resources from under the {@value com.nateyolles.sling.publick.PublickConstants#BLOG_PATH}
 * node (e.g. [/content/blog/2015, /content/blog/2016]).
 *
 * The Sightly component iterates over the resources 2 more levels deep to end
 * up with a full blog post path (e.g. /content/blog/2016/10/my-post).
 */
public class CollapsibleBlogList extends WCMUse {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollapsibleBlogList.class);

    /** Sightly component initialization. */
    @Override
    public void activate() {
    }

    /**
     * Get the immediate resources under the blog path that relate to blog posts
     * e.g. [/content/blog/2014, /content/blog/2015, /content/blog/2016].
     *
     * @return resources related to blog posts directly under the blog path
     */
    public Iterator<Resource> getPosts() {
        return getResourceResolver().findResources("SELECT * FROM [nt:unstructured] WHERE ISCHILDNODE('"
                + PublickConstants.BLOG_PATH + "') AND NAME() <> 'jcr:content'", Query.JCR_SQL2);
    }
}
