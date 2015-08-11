package com.nateyolles.sling.publick.components.admin;

import javax.script.Bindings;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.services.SystemSettingsService;

/**
 * Sightly component to get System Settings such as blog name.
 */
public class SystemConfig implements Use {

    /**
     * The Sling Script Helper to get services.
     */
    private SlingScriptHelper scriptHelper;

    /**
     * The current resource.
     */
    private Resource resource;

    /**
     * The name of the blog.
     */
    private String blogName;

    /**
     * The separator between blog name and page title.
     */
    private static final String TITLE_SEPARATOR = " - ";

    /**
     * The title property of the resource.
     */
    private static final String TITLE_PROPERTY = "title";

    /**
     * Initialize the Sightly component.
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);

        SystemSettingsService systemSettingsService = scriptHelper.getService(SystemSettingsService.class);

        if (systemSettingsService != null) {
            blogName = systemSettingsService.getBlogName();
        }
    }

    /**
     * Get the name of the blog.
     *
     * @return The name of the blog.
     */
    public String getBlogName() {
        return blogName;
    }

    /**
     * Get the title of the blog.
     *
     * The title of the blog is the blog post title, a separator (" - "),
     * and the name of the blog as configured.
     *
     * @return The title of the blog.
     */
    public String getTitle() {
        StringBuilder title = new StringBuilder();

        ValueMap properties = resource.adaptTo(ValueMap.class);
        String jcrTitle = properties.get("jcr:title", String.class);
        String titleProperty = properties.get(TITLE_PROPERTY, String.class);
        String resourceName = resource.getName();

        if (StringUtils.isNotBlank(jcrTitle)) {
            title.append(jcrTitle);
        } else if (StringUtils.isNotBlank(titleProperty)) {
            title.append(titleProperty);
        } else if (StringUtils.isNotBlank(resourceName)) {
            title.append(resourceName);
        }

        if (title.length() > 0 && StringUtils.isNotBlank(blogName)) {
            title.append(TITLE_SEPARATOR);
        }

        if (StringUtils.isNotBlank(blogName)) {
            title.append(blogName);
        }

        return title.toString();
    }
}