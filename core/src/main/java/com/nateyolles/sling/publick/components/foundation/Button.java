package com.nateyolles.sling.publick.components.foundation;

import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;

public class Button implements Use {

    private Resource resource;
    private SlingHttpServletRequest request;
    private String cssClass;

    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);

        ValueMap properties = resource.adaptTo(ValueMap.class);
        String size = properties.get("size", String.class); //large, default, small, extraSmall
        String style = properties.get("style", String.class); //default, primary, success, info, warning, danger, link
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

    public String getCssClass() {
        return cssClass;
    }
}