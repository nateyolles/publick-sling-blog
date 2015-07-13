package com.nateyolles.sling.publick.components.foundation;

import javax.script.Bindings;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;

/**
 * Button backed class for Sightly component.
 *
 * The Button component is based on Bootstrap and styled as such.
 */
public class Button implements Use {

    /**
     * The current component as a resource.
     */
    private Resource resource;

    /**
     * The Bootstrap matched CSS class for the button.
     */
    private String cssClass;

    /**
     * Initialization of the component.
     *
     * Reads the resource, gets the properties and creates the CSS
     * for the component.
     *
     * Options for "size" are:
     * <ul>
     * <li>large
     * <li>default
     * <li>small
     * <li>extraSmall
     * </ul>
     *
     * Options for "style" are:
     * <ul>
     * <li>default
     * <li>primary
     * <li>success
     * <li>info
     * <li>warning
     * <li>danger
     * <li>link
     * </ul>
     *
     * Options for "block" are true/false.
     */
    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);

        ValueMap properties = resource.adaptTo(ValueMap.class);
        String size = properties.get("size", String.class);
        String style = properties.get("style", String.class);
        boolean block = properties.get("block", Boolean.class);
        
        StringBuilder css = new StringBuilder("btn");
        
        if (size != null) {
            if (size.equals("large")) {
                css.append(" btn-lg");
            } else if (size.equals("small")) {
                css.append(" btn-sm");
            } else if (size.equals("extraSmall")) {
                css.append(" btn-xs");
            }
        }

        if (block) {
            css.append(" btn-block");
        }

        if (style != null) {
            if (style.equals("primary")) {
                css.append(" btn-primary");
            } else if (style.equals("success")) {
                css.append(" btn-success");
            } else if (style.equals("info")) {
                css.append(" btn-info");
            } else if (style.equals("warning")) {
                css.append(" btn-warning");
            } else if (style.equals("link")) {
                css.append(" btn-lnk");
            } else {
                css.append(" btn-default");
            }
        }

        cssClass = css.toString();
    }

    /**
     * Return the button's CSS class.
     *
     * @return the button's CSS class.
     */
    public String getCssClass() {
        return cssClass;
    }
}