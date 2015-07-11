package com.nateyolles.sling.publick.components.admin;

import java.util.Iterator;

import javax.jcr.query.Query;
import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.PublickConstants;

public class BlogList implements Use {

    private static final String BLOG_QUERY = "/jcr:root" + PublickConstants.BLOG_PATH + "//*[@" + JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY + "='" + PublickConstants.PAGE_TYPE_BLOG + "']";

    private Resource resource;

    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
    }

    public Iterator<Resource> getBlogs() {
        ResourceResolver resolver = resource.getResourceResolver();

        Iterator<Resource> blogs = resolver.findResources(BLOG_QUERY, Query.XPATH);

        return blogs;
    }
}