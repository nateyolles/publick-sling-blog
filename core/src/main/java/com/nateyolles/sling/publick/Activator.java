package com.nateyolles.sling.publick;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        LOGGER.info(bundleContext.getBundle().getSymbolicName() + " started");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        LOGGER.info(bundleContext.getBundle().getSymbolicName() + " stopped");
    }
}