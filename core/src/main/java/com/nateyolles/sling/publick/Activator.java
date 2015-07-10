package com.nateyolles.sling.publick;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.jcr.api.SlingRepository;

public class Activator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        LOGGER.info(bundleContext.getBundle().getSymbolicName() + " started");

        createGroups(bundleContext);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        LOGGER.info(bundleContext.getBundle().getSymbolicName() + " stopped");
    }

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

                    Authorizable authors = userManager.getAuthorizable("authors");
    
                    if (authors == null) {
                        authors = userManager.createGroup("authors");
                        authors.setProperty("displayName", valueFactory.createValue("Authors"));
                    }
                  
                    Authorizable testers = userManager.getAuthorizable("testers");
    
                    if (testers == null) {
                        testers = userManager.createGroup("testers");
                        testers.setProperty("displayName", valueFactory.createValue("Testers"));
                    }
                }
            } catch (RepositoryException e) {
                LOGGER.error("Could not get session.", e);
            } finally {
                if (session != null && session.isLive()) {
                    session.logout();
                    session = null;
                }
            }
        }
    }
}