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

/**
 * Sightly component for blog list/digest view pagination.
 */
public class BlogPagination implements Use {

    /**
     * The component property name which sets the page size.
     */
    private static final String PAGE_SIZE_PROPERTY = "pageSize";

    /**
     * The default number of blog posts on the list/digest page.
     */
    private static final int DEFAULT_PAGE_SIZE = 5;

    /**
     * The character that begins the suffix parameter.
     */
    private static final String SUFFIX_SEPARATOR = "/";

    /**
     * The current resource.
     */
    private Resource resource;

    /**
     * The Sling Script Helper.
     */
    private SlingScriptHelper scriptHelper;

    /**
     * The blog service which returns blog post counts.
     */
    private BlogService blogService;

    /**
     * The current HTTP request.
     */
    private SlingHttpServletRequest request;

    /**
     * The number of blog posts per page. Set in the component.
     */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /**
     * The current page index.
     */
    private int currentPage = 0;

    /**
     * The total number of pages.
     */
    private long totalPages = 0;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BlogPagination.class);

    /**
     * Initialize the Sightly component.
     *
     * Get services and properties. The entry point to the component.
     *
     * @param bindings The current execution context.
     */
    @Override
    public void init(Bindings bindings) {
        resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
        scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);
        blogService = scriptHelper.getService(BlogService.class);

        ValueMap properties = resource.adaptTo(ValueMap.class);
        pageSize = properties.get(PAGE_SIZE_PROPERTY, Integer.class);

        currentPage = getCurrentIndex();
        totalPages = getTotalPageCount();
    }

    /**
     * Get the total page count taking posts per page into account.
     *
     * @return The total page count.
     */
    private long getTotalPageCount() {
        long pages = 0;

        if (blogService != null) {
            pages = blogService.getNumberOfPages(pageSize);
        }

        return pages;
    }

    /**
     * Get the current page index, defaults to 1.
     *
     * @return The current page index.
     */
    private int getCurrentIndex() {
        int offset = 1;

        String suffix = request.getRequestPathInfo().getSuffix();

        if (suffix != null) {
            if (suffix.startsWith(SUFFIX_SEPARATOR)) {
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

    /**
     * Get the current page number.
     *
     * @return The current page number.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Get the total page count.
     *
     * @return The total page count.
     */
    public long getTotalPages(){
        return totalPages;
    }

    /**
     * Whether the current page is the first page.
     *
     * @return True if the current page is the first page.
     */
    public boolean getFirstPage() {
        return currentPage == 1;
    }

    /**
     * Whether the current page is the last page.
     *
     * @return True if the current page is the last page.
     */
    public boolean getLastPage() {
        return currentPage == totalPages;
    }

    /**
     * Get the path to the previous page.
     *
     * @return The path to the previous page.
     */
    public String getPreviousPath() {
        return getBlogListPagePath() + SUFFIX_SEPARATOR + (currentPage - 1);
    }

    /**
     * Get the path to the next page.
     *
     * @return The path to the next page.
     */
    public String getNextPath() {
        return getBlogListPagePath() + SUFFIX_SEPARATOR + (currentPage + 1);
    }

    /**
     * Get the list of pages and their paths.
     *
     * The list includes the index, whether the page is the current page
     * and the path of the page.
     *
     * @return The list of pages and their paths.
     */
    public List<HashMap<String, Object>> getPages() {
        List<HashMap<String, Object>> pages = new ArrayList<HashMap<String, Object>>();
        String path = getBlogListPagePath();

        for (int x = 1; x <= totalPages; x++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("page", x);
            map.put("path", path + SUFFIX_SEPARATOR + x);
            map.put("current", Boolean.valueOf(x == currentPage));
            pages.add(map);
        }

        return pages;
    }

    /**
     * Get the path to the blog list/digest page without any suffix.
     *
     * @return The path to the blog list/digest page.
     */
    private String getBlogListPagePath() {
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