package com.nateyolles.sling.publick.services;

import org.apache.sling.api.SlingHttpServletRequest;

public interface FileUploadService {
  String uploadFile(SlingHttpServletRequest request, String path);
}