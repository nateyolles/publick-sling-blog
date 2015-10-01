package com.nateyolles.sling.publick.services;

import java.util.List;

import org.apache.jackrabbit.vault.fs.api.ImportMode;
import org.apache.jackrabbit.vault.fs.io.AccessControlHandling;
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

    /**
     * Install package.
     *
     * Read about ImportModes: {@link http://jackrabbit.apache.org/filevault/importmode.html}.
     * Read about AccessControlHandling: {@link https://jackrabbit.apache.org/filevault/apidocs/org/apache/jackrabbit/vault/fs/io/AccessControlHandling.html}.
     *
     * @param request The current request.
     * @param groupName The name of the package group to install
     * @param packageName The name of the package to install
     * @param version The version of the package to install
     * @param importMode The import mode to use while installing
     * @param aclHandling The Access Control Handing to use while installing
     * @return true if package was installed successfully
     */
    boolean installPackage(final SlingHttpServletRequest request, final String groupName,
            final String packageName, final String version, final ImportMode importMode,
            final AccessControlHandling aclHandling);

    /**
     * Install backup package.
     *
     * Ignore access control so that authors can't upload a package and change them.
     * Replace all content so that it's a complete restore.
     *
     * @param request The current request.
     * @param packageName The name of the package to install
     * @return true if package was installed successfully
     */
    boolean installBackupPackage(final SlingHttpServletRequest request, final String packageName);

    /**
     * Upload a package.
     *
     * @param request The current Sling HTTP servlet request
     * @return The uploaded JCR Package
     */
    JcrPackage uploadBackupPackage(final SlingHttpServletRequest request);
}