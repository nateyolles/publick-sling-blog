package com.nateyolles.sling.publick.services.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.OsgiConfigurationService;

/**
 * Service to interact with OSGi configurations.
 */
@Service(value = OsgiConfigurationService.class)
@Component(metatype = true,
           immediate = true,
           name = "Publick OSGi Configuration Service",
           description = "Programatically set properties of OSGi configurations.")
public class OsgiConfigurationServiceImpl implements OsgiConfigurationService {

    /** The service to get OSGi configs */
    @Reference
    private ConfigurationAdmin configAdmin;

    /** The logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(OsgiConfigurationServiceImpl.class);

    /**
     * Set the value of an OSGi configuration property for a given PID.
     *
     * @param pid The PID of the OSGi component to update
     * @param property The property of the config to update
     * @param value The value to assign the provided property
     * @return true if the property was updated successfully
     */
    public boolean setProperty(final String pid, final String property, final Object value) {
        try {
            Configuration conf = configAdmin.getConfiguration(pid);

            @SuppressWarnings("unchecked")
            Dictionary<String, Object> props = conf.getProperties();

            if (props == null) {
                props = new Hashtable<String, Object>();
            }

            props.put(property, value != null ? value : StringUtils.EMPTY);
            conf.update(props);
        } catch (IOException e) {
            LOGGER.error("Could not set property", e);
            return false;
        }

        return true;
    }

    /**
     * Set the values of an OSGi configuration for a given PID.
     *
     * @param pid The PID of the OSGi component to update
     * @param properties The properties and values of the config to update
     * @return true if the properties were updated successfully
     */
    public boolean setProperties(final String pid, final Map<String, Object> properties) {
        try {
            Configuration conf = configAdmin.getConfiguration(pid);

            @SuppressWarnings("unchecked")
            Dictionary<String, Object> props = conf.getProperties();

            if (props == null) {
                props = new Hashtable<String, Object>();
            }

            /* props is of type org.apache.felix.cm.impl.CaseInsensitiveDictionary which
             * contains an internal HashTable and doesn't contain a putAll(Map) method.
             * Iterate over the map and put the values into the Dictionary individually.
             * Remove null values from HashMap as HashTable doesn't support them. */
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                props.put(key, value != null ? value : StringUtils.EMPTY);
            }

            conf.update(props);
        } catch (IOException e) {
            LOGGER.error("Could not set property", e);
            return false;
        }

        return true;
    }

    /**
     * Get the value of an OSGi configuration string property for a given PID.
     *
     * @param pid The PID of the OSGi component to retrieve
     * @param property The property of the config to retrieve
     * @param value The value to assign the provided property
     * @return The property value
     */
    public String getStringProperty(final String pid, final String property, final String defaultValue) {
        try {
            Configuration conf = configAdmin.getConfiguration(pid);

            @SuppressWarnings("unchecked")
            Dictionary<String, Object> props = conf.getProperties();

            if (props != null) {
                return PropertiesUtil.toString(props.get(property), defaultValue);
            }
        } catch (IOException e) {
            LOGGER.error("Could not get property", e);
        }

        return defaultValue;
    }

    /**
     * Get the value of an OSGi configuration boolean property for a given PID.
     *
     * @param pid The PID of the OSGi component to retrieve
     * @param property The property of the config to retrieve
     * @param value The value to assign the provided property
     * @return The property value
     */
    public boolean getBooleanProperty(final String pid, final String property, final boolean defaultValue) {
        try {
            Configuration conf = configAdmin.getConfiguration(pid);

            @SuppressWarnings("unchecked")
            Dictionary<String, Object> props = conf.getProperties();

            if (props != null) {
                return PropertiesUtil.toBoolean(props.get(property), defaultValue);
            }
        } catch (IOException e) {
            LOGGER.error("Could not get property", e);
        }

        return defaultValue;
    }

    /**
     * Get the value of an OSGi configuration long property for a given PID.
     *
     * @param pid The PID of the OSGi component to retrieve
     * @param property The property of the config to retrieve
     * @param value The value to assign the provided property
     * @return The property value
     */
    public Long getLongProperty(final String pid, final String property, final Long defaultValue) {
        long placeholder = -1L;
        long defaultTemp = defaultValue != null ? defaultValue : placeholder;

        try {
            Configuration conf = configAdmin.getConfiguration(pid);

            @SuppressWarnings("unchecked")
            Dictionary<String, Object> props = conf.getProperties();

            if (props != null) {
                long result = PropertiesUtil.toLong(props.get(property), defaultTemp);

                return result == placeholder ? null : result;
            }
        } catch (IOException e) {
            LOGGER.error("Could not get property", e);
        }

        return defaultValue;
    }

    /**
     * Wait for an OSGi service to become active.
     *
     * @param serviceImpl The service implementation class
     * @param timeout The length of time to wait for the service
     */
    private void waitForService(Class serviceImpl, long timeout) {
        Class serviceInterface = serviceImpl.getInterfaces()[0];
        BundleContext bundleContext = FrameworkUtil.getBundle(serviceInterface).getBundleContext();
        ServiceReference factoryRef = bundleContext.getServiceReference(serviceInterface.getName());

        ServiceTracker serviceTracker = new ServiceTracker(bundleContext, factoryRef, null);
        serviceTracker.open();

        try {
            serviceTracker.waitForService(timeout);
        } catch (InterruptedException e) {
            LOGGER.error("Could not get service", e);
        }

        serviceTracker.close();
    }
}