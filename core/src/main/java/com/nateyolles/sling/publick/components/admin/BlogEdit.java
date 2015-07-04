package com.nateyolles.sling.publick.components.admin;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import javax.jcr.query.Query;
import javax.script.Bindings;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.PublickConstants;

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

    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);

        String path = request.getRequestPathInfo().getSuffix();
        getBlog(path);
    }

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

    public String getKeywordsJSON() {
        JSONArray jsonArray = null;

        if (keywords != null) {
            jsonArray = new JSONArray(Arrays.asList(keywords));
        } else {
            jsonArray = new JSONArray();
        }

        return jsonArray.toString();
    }

    public String getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getMonths() {
        List<Long> months = new ArrayList<Long>();
        int length = 12;

        for (long x = 0; x < length; x++) {
            months.add(x + 1);
        }

        return months;
    }

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