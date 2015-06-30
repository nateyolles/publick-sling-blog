package com.nateyolles.sling.publick.services.impl;

import java.io.InputStream;
import java.util.Map;

import javax.jcr.Node;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.resource.JcrResourceUtil;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.FileUploadService;

@Service( value = FileUploadService.class )
@Component( metatype = true, immediate = true )
public class FileUploadServiceImpl implements FileUploadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadServiceImpl.class);

  public String uploadFile(SlingHttpServletRequest request, String path) {
      final RequestParameterMap params = request.getRequestParameterMap();
      ResourceResolver resolver = request.getResourceResolver();

      String filePath = null;

      for (final Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
          final String key = pairs.getKey();
          final RequestParameter[] pArr = pairs.getValue();
          final RequestParameter param = pArr[0];

          if (!param.isFormField()) {
              final String name = param.getFileName();
              final String mimeType = param.getContentType();

              try {
                  final InputStream stream = param.getInputStream();

                  Resource imagesParent = resolver.getResource(path);
                  Node imageNode = JcrUtils.putFile(imagesParent.adaptTo(Node.class), name, mimeType, stream);
                  resolver.commit();

                  filePath = imageNode.getPath();
              } catch (javax.jcr.RepositoryException e) {
                  LOGGER.error("Could not save image to repository.", e);
              } catch (java.io.IOException e) {
                  LOGGER.error("Could not get image input stream", e);
              }
          }
      }

      return filePath;
  }

  @Activate
  protected void activate(Map<String, Object> properties) {
  }

  @Deactivate
  protected void deactivate(ComponentContext ctx) {
  }
}