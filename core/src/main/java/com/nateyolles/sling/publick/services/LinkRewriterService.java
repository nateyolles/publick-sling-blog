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
    String rewriteLink(String link);

    /**
     * Rewrite all links in an HTML string based on the extensionless URLs settings.
     *
     * @param value The HTML string.
     * @return The HTML string with rewritten URLs.
     */
    String rewriteAllLinks(String html);
}