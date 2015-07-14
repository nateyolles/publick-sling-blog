package com.nateyolles.sling.publick.components.foundation;

import java.util.Arrays;

import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;

/**
 * Sightly component to display a single blog post.
 */
public class BlogView implements Use {

    /**
     * Selector to request view for displaying blog post in
     * list/digest view.
     */
    private static final String LIST_VIEW_SELECTOR = "list";

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
    private boolean listView;

    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);

        listView = Arrays.asList(request.getRequestPathInfo().getSelectors()).contains(LIST_VIEW_SELECTOR);

        getBlog(resource);
    }

    /**
     * Get the blog post properties from the resource.
     *
     * @param blog The blog post resource.
     */
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
            description = properties.get("description", String.class);
        }
    }

    /**
     * Get the blog post title.
     *
     * @return The blog post title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the blog post month selected by the author.
     *
     * @return The blog post month.
     */
    public Long getMonth() {
        return month;
    }

    /**
     * Get the blog post year selected by the author.
     *
     * @return The blog post year.
     */
    public Long getYear() {
        return year;
    }

    /**
     * Get the friendly URL set by the author.
     *
     * This is the node name.
     *
     * @return return the blog post node name.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the blog post visibility set by the author.
     *
     * @return The blog post visibility.
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Get the blog post keywords/tags set by the author.
     *
     * @return The blog post keywords/tags.
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * Get the blog post image path uploaded by the author.
     *
     * @return The blog post image path.
     */
    public String getImage() {
        return image;
    }

    /**
     * Get the blog post main content written by the author.
     *
     * @return The blog post main content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the blog post description written by the author.
     *
     * @return The blog post description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get whether the blog post is being requested in list view.
     *
     * @return Whether the blog post is being requested in list view.
     */
    public boolean getListView() {
        return listView;
    }
}