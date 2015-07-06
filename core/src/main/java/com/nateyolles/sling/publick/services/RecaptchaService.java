package com.nateyolles.sling.publick.services;

import org.apache.sling.api.SlingHttpServletRequest;

public interface RecaptchaService {
  String getSiteKey();

  String getSecretKey();

  boolean getEnabled();
}