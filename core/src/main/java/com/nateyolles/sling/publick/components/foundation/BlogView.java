package com.nateyolles.sling.publick.components.foundation;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.jcr.query.Query;
import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.PublickConstants;

public class BlogView implements Use {

    private Resource resource;
    private SlingHttpServletRequest request;
    private String title;
    private Long month;
    private Long year;
    private String url;
    private boolean visible;
    private String[] keywords;
    private String image;
    private String content;

    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);

        getBlog(resource);
    }

    private void getBlog(Resource blog) {
        if (blog != null) {
            ValueMap properties = blog.adaptTo(ValueMap.class);
            title = properties.get("title", String.class);
            month = properties.get("month", Long.class);
            year = properties.get("year", Long.class);
            url = properties.get("url", String.class);
            visible = Boolean.valueOf(properties.get("visible", false));
            keywords = properties.get("keywords", String[].class);
            image = properties.get("image", String.class);
            content = properties.get("content", String.class);
        }
    }

    public String getTitle() {
        return title;
    }

    public Long getMonth() {
        return month;
    }

    public Long getYear() {
        return year;
    }

    public String getUrl() {
        return url;
    }

    public boolean getVisible() {
        return visible;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public String getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }
}