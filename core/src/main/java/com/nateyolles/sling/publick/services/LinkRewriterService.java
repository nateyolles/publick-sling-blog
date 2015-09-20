package com.nateyolles.sling.publick.services;

/**
 * The APIs provided in order to interact with the link rewriter
 * to handle features such as rewriting links for extensionless
 * URLs.
 */
public interface LinkRewriterService {

    /**
     * Rewrite links based on the extensionless URLs settings.
     *
     * @param value The link URL.
     * @return The rewritten link URL.
     */
    public String rewriteLink(final String link);

    /**
     * Rewrite links based on the extensionless URLs settings.
     *
     * @param value The link URL.
     * @param requestHost The host name from the request.
     * @return The rewritten link URL.
     */
    String rewriteLink(String link, String requestHost);

    /**
     * Rewrite all links in an HTML string based on the extensionless URLs settings.
     *
     * @param value The HTML string.
     * @param requestHost The host name from the request.
     * @return The HTML string with rewritten URLs.
     */
    String rewriteAllLinks(String html, String requestHost);
}