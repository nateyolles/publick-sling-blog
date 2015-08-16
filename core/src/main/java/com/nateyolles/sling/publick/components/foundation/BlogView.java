package com.nateyolles.sling.publick.components.foundation;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.script.Bindings;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sightly component to display a single blog post.
 */
public class BlogView implements Use {

    /**
     * Logger instance to log and debug errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BlogView.class);

    /**
     * Selector to request view for displaying blog post in
     * list/digest view.
     */
    private static final String LIST_VIEW_SELECTOR = "list";

    private static final String PUBLISHED_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_FORMAT = "MMMM dd, yyyy";

    /**
     * The resource resolver to map paths.
     */
    private ResourceResolver resolver;

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

    /**
     * Blog post date in ISO-8601 format (e.g. 2015-07-29 or yyyy-MM-dd)
     * per Open Graph specifications.
     */
    private String publishedDate;

    /**
     * Display date in MMMM dd, yyyy format.
     */
    private String displayDate;

    /**
     * The bog post URL.
     */
    private String displayPath;

    /**
     * The full image URL.
     */
    private String displayImage;

    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
        resolver = resource.getResourceResolver();
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
            content = properties.get("content", String.class);
            description = properties.get("description", String.class);
            image = properties.get("image", String.class);

            if (image != null) {
                image = resolver.map(image);
            }

            Date date = properties.get(JcrConstants.JCR_CREATED, Date.class);

            publishedDate = getDate(date, PUBLISHED_DATE_FORMAT);
            displayDate = getDate(date, DISPLAY_DATE_FORMAT);

            displayPath = createDisplayPath();

            if (StringUtils.isNotBlank(image)) {
                displayImage = displayPath.replace(request.getRequestURI(), StringUtils.EMPTY) + image;
            }
        }
    }

    /**
     * Generate the blog post display path and remove "/content".
     *
     * @return The absolute blog post display path.
     */
    private String createDisplayPath() {
        final String path = resolver.map(resource.getPath());
        String displayPath = null;

        try {
            URL url = new URL(request.getRequestURL().toString());

            displayPath = new URL(url.getProtocol(), url.getHost(), url.getPort(), path).toString().concat(".html");
        } catch (MalformedURLException e) {
            LOGGER.error("Could not get DisplayPath from Request URL", e);
        }

        return displayPath;
    }

    /**
     * Format date in selected format.
     *
     * @param date The date.
     * @param format The format.
     * @return The formatted date.
     */
    private String getDate(final Date date, final String format) {
        String formattedDate = null;

        if (date != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
            formattedDate = dateFormatter.format(date);
        }

        return formattedDate;
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

    /**
     * Get the blog post published date in format "yyyy-MM-dd".
     *
     * @return The blog post published date.
     */
    public String getPublishedDate() {
        return publishedDate;
    }

    /**
     * Get the blog post display date in formation "MMMM dd, yyyy".
     *
     * @return The blog post display date.
     */
    public String getDisplayDate() {
        return displayDate;
    }

    /**
     * Get the blog post full URL.
     *
     * @return The blog post URL.
     */
    public String getDisplayPath() {
        return displayPath;
    }

    /**
     * Get the full image URL.
     *
     * @return The image URL.
     */
    public String getDisplayImage() {
        return displayImage;
    }
}