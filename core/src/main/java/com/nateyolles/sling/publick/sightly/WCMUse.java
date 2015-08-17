package com.nateyolles.sling.publick.sightly;

import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;

/**
 * The base class that all Sightly components should extend
 * from. Any sightly component extending this class should
 * simply override the #activate() method. Instead of using
 * Sling Bindings, the classes can use the provided helper
 * methods.
 */
public class WCMUse implements Use {

    /**
     * Global bindings populated by the init method.
     */
    private Bindings bindings;

    /**
     * Sightly component initialization.
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        this.bindings = bindings;
        activate();
    }

    /**
     * The activate method is meant to be overridden as it's the
     * entry point to the extended class.
     */
    public void activate() {
    }

    /**
     * Get the current resource.
     *
     * @return The current resource.
     */
    public Resource getResource() {
        return bindings != null ? (Resource)bindings.get(SlingBindings.RESOURCE) : null;
    }

    /**
     * Get the resource resolver backed by the current resource.
     *
     * @return The current resource resolver.
     */
    public ResourceResolver getResourceResolver() {
        return bindings != null ? ((Resource)bindings.get(SlingBindings.RESOURCE)).getResourceResolver() : null;
    }

    /**
     * Get the current resource properties.
     *
     * @return The current resource properties.
     */
    public ValueMap getProperties() {
        return bindings != null ? ((Resource)bindings.get(SlingBindings.RESOURCE)).adaptTo(ValueMap.class) : null;
    }

    /**
     * Get the current Sling Script Helper.
     *
     * @return The current Sling Script Helper.
     */
    public SlingScriptHelper getSlingScriptHelper() {
        return bindings != null ? (SlingScriptHelper)bindings.get(SlingBindings.SLING) : null;
    }

    /**
     * Get the current request.
     *
     * @return The current Sling HTTP Servlet Request.
     */
    public SlingHttpServletRequest getRequest() {
        return bindings != null ? (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST) : null;
    }

    /**
     * Get the current response.
     *
     * @return The current Sling HTTP Servlet Response.
     */
    public SlingHttpServletResponse getResponse() {
        return bindings != null ? (SlingHttpServletResponse)bindings.get(SlingBindings.RESPONSE) : null;
    }
}