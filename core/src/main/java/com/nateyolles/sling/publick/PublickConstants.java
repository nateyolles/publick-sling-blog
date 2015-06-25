package com.nateyolles.sling.publick;

public class PublickConstants {

    private PublickConstants() {
    }

    public static final String CONTENT_PATH = "/content";
    public static final String BLOG_PATH = CONTENT_PATH + "/blog";
    public static final String ADMIN_PATH = CONTENT_PATH + "/admin";
    public static final String ASSET_PATH = CONTENT_PATH + "/assets";
    public static final String IMAGE_PATH = CONTENT_PATH + "/images";

    private static final String NODE_TYPE = "publick";
    public static final String NODE_TYPE_PAGE = NODE_TYPE + ":page";
    public static final String NODE_TYPE_TEMPLATE = NODE_TYPE + ":template";
    public static final String NODE_TYPE_COMPONENT = NODE_TYPE + ":component";

    private static final String PAGE_TYPE = "publick/pages";
    public static final String PAGE_TYPE_ADMIN = PAGE_TYPE + "/adminPage";
    public static final String PAGE_TYPE_BASIC = PAGE_TYPE + "/basicPage";
    public static final String PAGE_TYPE_BLOG = PAGE_TYPE + "/blogPage";
}