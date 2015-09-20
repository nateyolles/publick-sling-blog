package com.nateyolles.sling.publick.services.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ac.simons.akismet.Akismet;
import ac.simons.akismet.AkismetComment;
import ac.simons.akismet.AkismetException;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.HttpService;
import com.nateyolles.sling.publick.services.LinkRewriterService;
import com.nateyolles.sling.publick.services.OsgiConfigurationService;
import com.nateyolles.sling.publick.services.AkismetService;

import org.osgi.framework.Constants;

/**
 * Akismet service to get and set settings and communicate with
 * www.akismet.com. Verify your key, validate comments, submit
 * spam and ham.
 *
 * Checking comments returns whether the comment is spam. That
 * means a false return means the comment is valid while a true
 * return means the comment is spam. To test, send
 * "viagra-test-123" as the author and it will always trigger a
 * true response.
 *
 * For false positives, a comment determined to be spam that isn't,
 * correct the situation by submitting ham.
 *
 * To identify a comment as spam that wasn't caught before, submit
 * the comment as spam.
 */
@Service(value = AkismetService.class)
@Component(metatype = true,
           immediate = true,
           name = "Publick Akismet settings",
           description = "Akismet settings for www.akismet.com's service.")
@Properties({
    @Property(name = AkismetServiceImpl.AKISMET_API_KEY,
              label = "API Key",
              description = "The Akismet API key."),
    @Property(name = AkismetServiceImpl.AKISMET_DOMAIN_NAME,
              label = "Domain Name",
              description = "The domain name of the blog starting with http:// or https://"),
    @Property(name = AkismetServiceImpl.AKISMET_ENABLED,
              boolValue = AkismetServiceImpl.ENABLED_DEFAULT_VALUE,
              label = "Enabled",
              description = "Enable Akismet."),
    @Property(name = Constants.SERVICE_DESCRIPTION,
              value = "Akismet settings for www.akismet.com's service."),
    @Property(name = Constants.SERVICE_VENDOR,
              value = "Publick")
})
public class AkismetServiceImpl implements AkismetService {

    /** Service to get and set OSGi properties. */
    @Reference
    private OsgiConfigurationService osgiService;

    /** HTTP helpers */
    @Reference
    private HttpService httpService;

    /** Rewrite links to get permanent path to comment */
    @Reference
    private LinkRewriterService linkRewriter;

    /** PID of the current OSGi component */
    private static final String COMPONENT_PID = "Publick Akismet settings";

    /** Default value for enabled */
    public static final boolean ENABLED_DEFAULT_VALUE = false;

    /** Type of Akismet submission */
    public static final String AKISMET_COMMENT_TYPE = "comment";

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AkismetServiceImpl.class);

    /**
     * Set multiple properties for the Akismet Settings service.
     *
     * This is useful for setting multiple properties as the same
     * time in that the OSGi component will only be updated once
     * and thus reset only once.
     *
     * @param properties A map of properties to set.
     * @return true if save was successful.
     */
    public boolean setProperties(final Map<String, Object> properties) {
        return osgiService.setProperties(COMPONENT_PID, properties);
    }

    /**
     * Is the Akismet service enabled.
     *
     * @return True if the Akismet service is enabled.
     */
    public boolean getEnabled() {
        return osgiService.getBooleanProperty(COMPONENT_PID, AKISMET_ENABLED, ENABLED_DEFAULT_VALUE);
    }

    /**
     * Set whether the Akismet service is enabled.
     *
     * @param enabled The enabled property to set.
     * @return true if the save was successful.
     */
    public boolean setEnabled(final boolean enabled) {
        return osgiService.setProperty(COMPONENT_PID, AKISMET_ENABLED, enabled);
    }

    /**
     * Get the Akismet API key.
     *
     * @return The Akismet API key.
     */
    public String getApiKey() {
        return osgiService.getStringProperty(COMPONENT_PID, AKISMET_API_KEY, null);
    }

    /**
     * Set the Akismet API key.
     *
     * @param apiKey The API key property to set.
     * @return true if the save was successful.
     */
    public boolean setApiKey(final String apiKey) {
        return osgiService.setProperty(COMPONENT_PID, AKISMET_API_KEY, apiKey);
    }

    /**
     * Get the Akismet domain name.
     *
     * @return The Akismet domain name.
     */
    public String getDomainName() {
        return osgiService.getStringProperty(COMPONENT_PID, AKISMET_DOMAIN_NAME, null);
    }

    /**
     * Set the Akismet domain name.
     *
     * @param domainName The domain nameproperty to set.
     * @return true if the save was successful.
     */
    public boolean setDomainName(final String domainName) {
        return osgiService.setProperty(COMPONENT_PID, AKISMET_DOMAIN_NAME, domainName);
    }

    /**
     * Verify that Akismet is configured correctly using the saved
     * API key and domain name settings.
     *
     * Sends a request to the Akismet servers with the API key and
     * domain name.
     *
     * @return true if Akismet is configured correctly.
     */
    public boolean verifyKey() {
        return verifyKey(getApiKey(), getDomainName());
    }

    /**
     * Verify that Akismet is configured correctly using the given
     * API key and domain name.
     *
     * Sends a request to the Akismet servers with the API key and
     * domain name.
     *
     * @param apiKey The Akismet API key to test.
     * @param domainName The Akismet domain name to test.
     * @return true if Akismet is configured correctly.
     */
    public boolean verifyKey(final String apiKey, final String domainName) {
        final Akismet akismet = new Akismet(HttpClients.createDefault());
        boolean result = false;

        akismet.setApiKey(apiKey);
        akismet.setApiConsumer(domainName);

        try {
            result = akismet.verifyKey();
        } catch (AkismetException e) {
            LOGGER.error("Could not verify Akismet Key", e);
        }

        return result;
    }

    /**
     * Check comment against Akismet servers.
     *
     * Be aware that this method returns whether the submission is spam or not.
     * A false response means that the submission was successful and that the
     * comment is not spam. This behavior is inline with Akismet's behavior.
     *
     * @param commentResource The publick:comment resource to act upon.
     * @return true if comment is spam, false if comment is valid.
     */
    public boolean isSpam(final Resource commentResource) {
        return doAkismet(AkismetAction.CHECK_COMMENT, commentResource);
    }

    /**
     * Submit comment as spam to the Akismet servers.
     *
     * If a comment gets checked and incorrectly is reported as ham, this will
     * submit it back to the servers as spam to help make the world a better
     * place.
     *
     * @param commentResource The publick:comment resource to act upon.
     * @return true if submission was successful.
     */
    public boolean submitSpam(final Resource commentResource) {
        return doAkismet(AkismetAction.SUBMIT_SPAM, commentResource);
    }

    /**
     * Submit comment as ham to the Akismet servers.
     *
     * If a comment gets checked and incorrectly is reported as spam, this will
     * submit it back to the servers as ham to correct a false positive.
     *
     * @param commentResource The publick:comment resource to act upon.
     * @return true if submission was successful.
     */
    public boolean submitHam(final Resource commentResource) {
        return doAkismet(AkismetAction.SUBMIT_HAM, commentResource);
    }

    /**
     * Perform Akismet actions
     *
     * @param action The Akismet action to perform
     * @param commentResource The publick:comment resource to act upon.
     * @return True True if Spam or Ham submission was successful.
     */
    private boolean doAkismet(final AkismetAction action, final Resource commentResource) {
        final Akismet akismet = new Akismet(HttpClients.createDefault());
        final AkismetComment comment = getAkismetComment(commentResource);

        akismet.setApiKey(getApiKey());
        akismet.setApiConsumer(getDomainName());

        boolean result = false;

        try {
            if (action == AkismetAction.CHECK_COMMENT) {
                result = akismet.commentCheck(comment);
            } else if (action == AkismetAction.SUBMIT_SPAM) {
                result = akismet.submitSpam(comment);
            } else if (action == AkismetAction.SUBMIT_HAM) {
                result = akismet.submitHam(comment);
            }
        } catch (AkismetException e) {
            LOGGER.error("Could not communication with Akismet", e);
        }

        return result;
    }

    /**
     * Get an AkismetComment from a publick:comment resource.
     *
     * @param commentResource The publick:comment resource.
     * @return Akismet Comment created from the properties of the comment resource.
     */
    private AkismetComment getAkismetComment(final Resource commentResource) {
        final AkismetComment akismetComment = new AkismetComment();

        if (commentResource != null) {
            final ValueMap properties = commentResource.getValueMap();

            // Get external link taking extensionless URLs into account
            String externalLink = StringUtils.removeEnd(getDomainName(), "/");
            externalLink = externalLink.concat(linkRewriter.rewriteLink(commentResource.getPath()));

            akismetComment.setUserIp(properties.get(PublickConstants.COMMENT_PROPERTY_USER_IP, String.class));
            akismetComment.setUserAgent(properties.get(PublickConstants.COMMENT_PROPERTY_USER_AGENT, String.class));
            akismetComment.setReferrer(properties.get(PublickConstants.COMMENT_PROPERTY_REFERRER, String.class));
            akismetComment.setPermalink(externalLink);
            akismetComment.setCommentType(AKISMET_COMMENT_TYPE);
            akismetComment.setCommentAuthor(properties.get(PublickConstants.COMMENT_PROPERTY_AUTHOR, String.class));
            akismetComment.setCommentContent(properties.get(PublickConstants.COMMENT_PROPERTY_COMMENT, String.class));
        }

        return akismetComment;
    }

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
     * Akismet actions
     */
    private enum AkismetAction {
        CHECK_COMMENT,
        SUBMIT_SPAM,
        SUBMIT_HAM
    }
}