package com.nateyolles.sling.publick;

public class PublickConstants {

    private PublickConstants() {
    }

    public static final String ROOT_PATH = "/";

    public static final String CONTENT_PATH = "/content";
    public static final String BLOG_PATH = CONTENT_PATH + "/blog";
    public static final String ADMIN_PATH = CONTENT_PATH + "/admin";
    public static final String ASSET_PATH = CONTENT_PATH + "/assets";
    public static final String IMAGE_PATH = ASSET_PATH + "/images";
    public static final String PDF_PATH = ASSET_PATH + "/pdf";
    public static final String AUDIO_PATH = ASSET_PATH + "/audio";
    public static final String VIDEO_PATH = ASSET_PATH + "/video";

    public static final String ADMIN_LANDING_PATH = ADMIN_PATH + "/index";
    public static final String ADMIN_LIST_PATH = ADMIN_PATH + "/list";
    public static final String ADMIN_EDIT_PATH = ADMIN_PATH + "/edit";
    public static final String ADMIN_ASSETS_PATH = ADMIN_PATH + "/assets";
    public static final String ADMIN_CONFIG_PATH = ADMIN_PATH + "/config";

    private static final String NODE_TYPE = "publick";
    public static final String NODE_TYPE_PAGE = NODE_TYPE + ":page";
    public static final String NODE_TYPE_TEMPLATE = NODE_TYPE + ":template";
    public static final String NODE_TYPE_COMPONENT = NODE_TYPE + ":component";

    private static final String PAGE_TYPE = "publick/pages";
    public static final String PAGE_TYPE_ADMIN = PAGE_TYPE + "/adminPage";
    public static final String PAGE_TYPE_BASIC = PAGE_TYPE + "/basicPage";
    public static final String PAGE_TYPE_BLOG = PAGE_TYPE + "/blogPage";

    public static final String CONFIG_PATH = "/etc/config";
    public static final String CONFIG_RECAPTCHA_PATH =  CONFIG_PATH + "/recaptcha";

    public static final String GROUP_ID_AUTHORS = "authors";
    public static final String GROUP_ID_TESTERS = "testers";
    public static final String GROUP_DISPLAY_AUTHORS = "Authors";
    public static final String GROUP_DISPLAY_TESTERS = "Testers";
}