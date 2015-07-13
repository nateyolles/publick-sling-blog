package com.nateyolles.sling.publick;

import java.security.Principal;
import java.util.NoSuchElementException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.security.Privilege;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;

/**
 * Setup application by creating user groups and setting privileges.
 * This class runs on activation of the core bundle.
 */
public class Activator implements BundleActivator {

    /**
     * Logger instance to log and debug errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    /**
     * rep:write mixin.
     */
    private static final String REP_WRITE = "rep:write";

    /**
     * Display Name property for user groups.
     */
    private static final String GROUP_DISPLAY_NAME = "displayName";

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        LOGGER.info(bundleContext.getBundle().getSymbolicName() + " started");

        createGroups(bundleContext);
        setPermissions(bundleContext);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        LOGGER.info(bundleContext.getBundle().getSymbolicName() + " stopped");
    }

    /**
     * Set blog and asset paths as writable for the author user groups.
     *
     * @param bundleContext The bundle context provided by the component.
     */
    private void setPermissions(BundleContext bundleContext) {
       setWritable(bundleContext, PublickConstants.BLOG_PATH);
       setWritable(bundleContext, PublickConstants.ASSET_PATH);
    }

    /**
     * Set resource as writable for the author user group.
     *
     * @param bundleContext The bundle context provided by the component.
     * @param path The resource path to update permissions.
     */
    private void setWritable(BundleContext bundleContext, String path) {
        ServiceReference ResourceResolverFactoryReference = bundleContext.getServiceReference(ResourceResolverFactory.class.getName());
        ResourceResolverFactory resolverFactory = (ResourceResolverFactory)bundleContext.getService(ResourceResolverFactoryReference);

        if (resolverFactory != null) {
            ResourceResolver resolver = null;

            try {
                resolver = resolverFactory.getAdministrativeResourceResolver(null);

                Resource resource = resolver.getResource(path);

                if (resource != null) {
                    JackrabbitSession session = (JackrabbitSession)resolver.adaptTo(Session.class);
                    JackrabbitAccessControlManager accessControlManager = (JackrabbitAccessControlManager)session.getAccessControlManager();

                    Group user = (Group)session.getUserManager().getAuthorizable(PublickConstants.GROUP_ID_AUTHORS);
                    Principal principal = user.getPrincipal();

                    Privilege[] privileges = new Privilege[] {
                        accessControlManager.privilegeFromName(Privilege.JCR_WRITE),
                        accessControlManager.privilegeFromName(REP_WRITE)
                    };
                    JackrabbitAccessControlList acl;

                    try {
                        acl = (JackrabbitAccessControlList)accessControlManager.getApplicablePolicies(path).nextAccessControlPolicy();
                    } catch (NoSuchElementException e) {
                        acl = (JackrabbitAccessControlList) accessControlManager.getPolicies(path)[0];
                    }

                    acl.addEntry(principal, privileges, true);
                    accessControlManager.setPolicy(path, acl);

                    session.save();
                }
            } catch (LoginException e) {
                LOGGER.error("Could not login to repository", e);
            } catch (RepositoryException e){
                LOGGER.error("Could not save to repository", e);
            } finally {
                if (resolver != null && resolver.isLive()) {
                    resolver.close();
                    resolver = null;
                }
            }
        }
    }

    /**
     * Create user groups for authors and testers.
     *
     * @param bundleContext The bundle context provided by the component.
     */
    private void createGroups(BundleContext bundleContext){
        ServiceReference SlingRepositoryFactoryReference = bundleContext.getServiceReference(SlingRepository.class.getName());
        SlingRepository repository = (SlingRepository)bundleContext.getService(SlingRepositoryFactoryReference);

        Session session = null;

        if (repository != null) {
            try {
                session = repository.loginAdministrative(null);

                if (session != null && session instanceof JackrabbitSession) {
                    UserManager userManager = ((JackrabbitSession)session).getUserManager();
                    ValueFactory valueFactory = session.getValueFactory();

                    Authorizable authors = userManager.getAuthorizable(PublickConstants.GROUP_ID_AUTHORS);

                    if (authors == null) {
                        authors = userManager.createGroup(PublickConstants.GROUP_ID_AUTHORS);
                        authors.setProperty(GROUP_DISPLAY_NAME, valueFactory.createValue(PublickConstants.GROUP_DISPLAY_AUTHORS));
                    }

                    Authorizable testers = userManager.getAuthorizable(PublickConstants.GROUP_ID_TESTERS);

                    if (testers == null) {
                        testers = userManager.createGroup(PublickConstants.GROUP_ID_TESTERS);
                        testers.setProperty(GROUP_DISPLAY_NAME, valueFactory.createValue(PublickConstants.GROUP_DISPLAY_TESTERS));
                    }
                }
            } catch (RepositoryException e) {
                LOGGER.error("Could not get session", e);
            } finally {
                if (session != null && session.isLive()) {
                    session.logout();
                    session = null;
                }
            }
        }
    }
}