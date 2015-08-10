package com.nateyolles.sling.publick.components.admin;

import javax.script.Bindings;

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
     * The name of the blog.
     */
    private String blogName;

    /**
     * Initialize the Sightly component.
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);

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
}