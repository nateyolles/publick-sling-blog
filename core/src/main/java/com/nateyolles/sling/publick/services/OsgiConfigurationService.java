package com.nateyolles.sling.publick.services;

import java.util.Map;

/**
 * The APIs provided in order to interact with OSGi configurations
 */
public interface OsgiConfigurationService {

    /**
     * Set the value of an OSGi configuration property for a given PID.
     *
     * @param pid The PID of the OSGi component to update
     * @param property The property of the config to update
     * @param value The value to assign the provided property
     * @return true if the property was updated successfully
     */
    boolean setProperty(final String pid, final String property, final Object value);

    /**
     * Set the values of an OSGi configuration for a given PID.
     *
     * @param pid The PID of the OSGi component to update
     * @param properties The properties and values of the config to update
     * @return true if the properties were updated successfully
     */
    boolean setProperties(final String pid, final Map<String, Object> properties);

    /**
     * Get the value of an OSGi configuration string property for a given PID.
     *
     * @param pid The PID of the OSGi component to retrieve
     * @param property The property of the config to retrieve
     * @param value The value to assign the provided property
     * @return The property value
     */
    String getStringProperty(final String pid, final String property, final String defaultValue);

    /**
     * Get the value of an OSGi configuration boolean property for a given PID.
     *
     * @param pid The PID of the OSGi component to retrieve
     * @param property The property of the config to retrieve
     * @param value The value to assign the provided property
     * @return The property value
     */
    boolean getBooleanProperty(final String pid, final String property, final boolean defaultValue);

    /**
     * Get the value of an OSGi configuration long property for a given PID.
     *
     * @param pid The PID of the OSGi component to retrieve
     * @param property The property of the config to retrieve
     * @param value The value to assign the provided property
     * @return The property value
     */
    Long getLongProperty(final String pid, final String property, final Long defaultValue);
}