package com.nateyolles.sling.publick.components.foundation;

import org.apache.sling.api.resource.ValueMap;

import com.nateyolles.sling.publick.sightly.WCMUse;

/**
 * Button backed class for Sightly component.
 *
 * The Button component is based on Bootstrap and styled as such.
 */
public class Button extends WCMUse {

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
    public void activate() {

        ValueMap properties = getProperties();
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