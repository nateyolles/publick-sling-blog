package com.nateyolles.sling.publick.services.impl;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.ComponentContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.LinkRewriterService;
import com.nateyolles.sling.publick.services.SystemSettingsService;

/**
 * Service to rewrite links according to the user defined system settings value
 * for extensionless URLs. If extensionless URLs is enabled, rewrite paths by
 * removing ".html" extensions, removing trailing slashes, and removing index
 * file names.
 */
@Service( value = LinkRewriterService.class )
@Component( metatype = true, immediate = true )
public class LinkRewriterServiceImpl implements LinkRewriterService {

    @Reference
    SystemSettingsService systemSettingsService;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkRewriterServiceImpl.class);

    /**
     * Service activation.
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
    }

    /**
     * Service Deactivation.
     *
     * @param ctx The current component context.
     */
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
    }

    /**
     * Rewrite links based on the extensionless URLs settings.
     *
     * @param value The link URL.
     * @param requestHost The host name from the request.
     * @return The rewritten link URL.
     */
    public String rewriteLink(final String link, final String requestHost) {
        String newLink = link;

        try {
            URI uri = new URI(link);
            String linkHost = uri.getHost();

            if (linkHost == null || linkHost.equals(requestHost)) {
                String path = uri.getPath();

                path = StringUtils.removeStart(path, "/content");

                if (systemSettingsService != null && systemSettingsService.getExtensionlessUrls()
                        && StringUtils.isNotBlank(link)) {

                    path = StringUtils.removeEnd(path, ".html");
                    path = StringUtils.removeEnd(path, "index");
                    path = StringUtils.removeEnd(path, "/");
                } else if (StringUtils.isNotBlank(path) && !path.endsWith("/") && !path.endsWith(".html")) {
                    path = path.concat(".html");
                }

                newLink = new URI(null, null, path, uri.getQuery(), uri.getFragment()).toString();
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Could not rewrite link do to link syntax.", e);
        }

        return newLink;
    }

    /**
     * Rewrite all links in an HTML string based on the extensionless URLs settings.
     *
     * @param value The HTML string.
     * @param requestHost The host name from the request.
     * @return The HTML string with rewritten URLs.
     */
    public String rewriteAllLinks(final String html, final String requestHost) {
        Document document = Jsoup.parse(html);
        Elements links = document.select("a[href]");

        for (Element link : links)  {
            String newLink = rewriteLink(link.attr("href"), requestHost);
            link.attr("href", newLink);
        }

        return document.toString();
    }
}