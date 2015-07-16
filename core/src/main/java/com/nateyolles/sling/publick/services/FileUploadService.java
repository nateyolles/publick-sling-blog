package com.nateyolles.sling.publick.services;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Provides the API for service that allows file uploads. Files
 * are mostly images uploaded by the author but could be any
 * file type.
 */
public interface FileUploadService {

    /**
     * Upload the file and save it as a node in the JCR.
     *
     * @param request The request in order to get the resource resolver.
     * @param path The path of the parent node to save the file under.
     * @return The path of new file in the JCR.
     */
    String uploadFile(SlingHttpServletRequest request, String path);
}