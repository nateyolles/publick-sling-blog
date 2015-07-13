package com.nateyolles.sling.publick.components.foundation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.Bindings;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.BlogService;

public class BlogPagination implements Use {

    private Resource resource;
    private SlingScriptHelper scriptHelper;
    private BlogService blogService;
    private SlingHttpServletRequest request;

    private int pageSize = 0;
    private int currentPage = 0;
    private long totalPages = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogPagination.class);

    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
        scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);
        blogService = scriptHelper.getService(BlogService.class);

        ValueMap properties = resource.adaptTo(ValueMap.class);
        pageSize = properties.get("pageSize", Integer.class);

        currentPage = getCurrentIndex();
        totalPages = getTotalPageCount();
    }

    private long getTotalPageCount() {
        long pages = 0;

        if (blogService != null) {
            pages = blogService.getNumberOfPages(pageSize);
        }

        return pages;
    }

    private int getCurrentIndex() {
        int offset = 1;

        String suffix = request.getRequestPathInfo().getSuffix();

        if (suffix != null) {
            if (suffix.startsWith("/")) {
                suffix = suffix.substring(1);
            }

            try {
                offset = Integer.valueOf(suffix);
            } catch (NumberFormatException e) {
                LOGGER.error("Could not get offset", e);
            }
        }

       return offset;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getTotalPages(){
        return totalPages;
    }

    public boolean getFirstPage() {
        return currentPage == 1;
    }

    public boolean getLastPage() {
        return currentPage == totalPages;
    }

    public String getPreviousPath() {
        return getSafePath() + "/" + (currentPage - 1);
    }

    public String getNextPath() {
        return getSafePath() + "/" + (currentPage + 1);
    }

    public List<HashMap<String, Object>> getPages() {
        List<HashMap<String, Object>> pages = new ArrayList<HashMap<String, Object>>();
        String path = getSafePath();

        for (int x = 1; x <= totalPages; x++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("page", x);
            map.put("path", path + "/" + x);
            map.put("current", Boolean.valueOf(x == currentPage));
            pages.add(map);
        }

        return pages;
    }

    private String getSafePath() {
        String path = request.getRequestURI();
        String suffix = request.getRequestPathInfo().getSuffix();

        if (suffix != null) {
            int indexOfSuffix = path.indexOf(suffix);

            if (indexOfSuffix != -1) {
                path = path.substring(0, path.indexOf(suffix));
            }
        }

        return path;
    }
}