package com.nateyolles.sling.publick.components.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.script.Bindings;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.scripting.sightly.pojo.Use;

/**
 * Sightly component to edit blog posts in the admin section. The
 * component determines whether to create a new blog post or edit
 * and existing blog post. To edit an existing blog post, pass
 * the resource path in the URL as the suffix.
 */
public class BlogEdit implements Use {

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
    private String description;

    /**
     * Sightly component initialization.
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);

        String path = request.getParameter("post");

        if (StringUtils.isNotBlank(path)) {
            getBlog(path);
        }
    }

    /**
     * Get the blog post properties if resource already exists otherwise
     * set the month and year properties to the current date.
     *
     * @param path The resource path to the blog post.
     */
    private void getBlog(String path) {
        ResourceResolver resolver = resource.getResourceResolver();
        Resource blog = resolver.getResource(path);

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
            description = properties.get("description", String.class);
            url = blog.getName();
        } else {
            /* Populate dropdowns with current date if creating new blog. */
            month = ((long)Calendar.getInstance().get(Calendar.MONTH)) + 1;
            year = (long)Calendar.getInstance().get(Calendar.YEAR);
        }
    }

    /**
     * Get the title property.
     *
     * @return The title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the month property.
     *
     * @return The month property.
     */
    public Long getMonth() {
        return month;
    }

    /**
     * Get the year property.
     *
     * @return The year property.
     */
    public Long getYear() {
        return year;
    }

    /**
     * Get the resource name of the blog post URL.
     *
     * @return The resource name of hte blog post URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the visible property.
     *
     * @return The visible property.
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Get the multi-value keyword property.
     *
     * @return The multi-value keyword property.
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * Get the multi-value keywords property as a JSON string.
     *
     * @return The multi-value keyword property as a JSON string.
     */
    public String getKeywordsJSON() {
        JSONArray jsonArray = null;

        if (keywords != null) {
            jsonArray = new JSONArray(Arrays.asList(keywords));
        } else {
            jsonArray = new JSONArray();
        }

        return jsonArray.toString();
    }

    /**
     * Get the image path property.
     *
     * @return The image path property.
     */
    public String getImage() {
        return image;
    }

    /**
     * Get the blog post content.
     *
     * @return The blog post content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the description property.
     *
     * @return The description property.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the month property.
     *
     * @return The month property.
     */
    public List<Long> getMonths() {
        List<Long> months = new ArrayList<Long>();
        int length = 12;

        for (long x = 0; x < length; x++) {
            months.add(x + 1);
        }

        return months;
    }

    /**
     * Get the year property.
     *
     * @return the year property.
     */
    public List<Long> getYears() {
        List<Long> years = new ArrayList<Long>();
        int preYears = 2;
        int postYears = 2;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (long x = currentYear - preYears; x <= currentYear + postYears; x++) {
            years.add(x);
        }

        return years;
    }
}