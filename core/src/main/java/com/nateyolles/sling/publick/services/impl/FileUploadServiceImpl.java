package com.nateyolles.sling.publick.services.impl;

import java.io.InputStream;
import java.util.Map;

import javax.jcr.Node;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.jackrabbit.commons.JcrUtils;


import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.services.FileUploadService;

/**
 * Service to handle file uploading.
 */
@Service( value = FileUploadService.class )
@Component( metatype = true, immediate = true )
public class FileUploadServiceImpl implements FileUploadService {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    /**
     * Uploads a file from a POST request to the specified location.
     *
     * TODO: upload multiple files and return array
     *
     * @param request The Sling HTTP servlet request.
     * @param path The path of the parent node to save the file under.
     */
    public String uploadFile(SlingHttpServletRequest request, String path) {
        final RequestParameterMap params = request.getRequestParameterMap();
        ResourceResolver resolver = request.getResourceResolver();

        String filePath = null;

        for (final Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
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

    /**
     * Activate service.
     *
     * @param properties
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
    }

    /**
     * Deactivate service.
     * @param ctx
     */
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
    }
}