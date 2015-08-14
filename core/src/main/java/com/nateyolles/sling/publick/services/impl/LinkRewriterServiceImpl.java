package com.nateyolles.sling.publick.services.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
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
     * @return The rewritten link URL.
     */
    public String rewriteLink(final String link) {
        String newLink = link;

        try {
            URI uri = new URI(link);

            if (uri.getHost() == null) {
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

    public String rewriteAllLinks(final String html) {
        Document document = Jsoup.parse(html);
        Elements links = document.select("a[href]");

        for (Element link : links)  {
            link.attr("href", rewriteLink(link.attr("href")));
        }

        return document.toString();
    }
}