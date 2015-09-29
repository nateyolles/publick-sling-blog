package com.nateyolles.sling.publick.services;

import java.util.List;

import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * API's to get, create, upload, install and delete packages.
 */
public interface PackageService {

    /**
     * Get the list of all packages in order of newest to oldest.
     *
     * @param request The Sling HTTP servlet request
     * @return List of all JCR Packages
     */
    List<JcrPackage> getPackageList(final SlingHttpServletRequest request);

    /**
     * Create a JCR package and store it under /etc/packages/group_name/package_name-version.zip.
     *
     * @param request The Sling HTTP servlet request
     * @param groupName The name of the package group
     * @param packageName The name of the package
     * @param version The version of the package
     * @param paths The JCR paths to include in the package
     * @return the saved JCR Package
     */
    JcrPackage createPackage(final SlingHttpServletRequest request, final String groupName,
            final String packageName, final String version, final String[] paths);

    /**
     * Create a Publick backup package.
     *
     * @param request The Sling HTTP servlet request
     * @param packageName The name of the package
     * @return the saved JCR Package
     */
    JcrPackage createBackupPackage(final SlingHttpServletRequest request, final String packageName);
}