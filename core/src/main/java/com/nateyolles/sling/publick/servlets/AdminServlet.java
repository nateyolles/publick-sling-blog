package com.nateyolles.sling.publick.servlets;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for admin configuration servlets.
 */
public abstract class AdminServlet extends SlingAllMethodsServlet {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServlet.class);

    /**
     * Send the JSON response.
     *
     * @param writer The PrintWriter.
     * @param header The header to send.
     * @param message The message to send.
     */
    protected void sendResponse(PrintWriter writer, String header, String message) {
        try {
            writer.write(new JSONObject()
                .put("header", header)
                .put("message", message)
                .toString());
        } catch (JSONException e) {
            LOGGER.error("Could not write JSON", e);
            writer.write(String.format("{\"header\" : \"%s\", \"message\" : \"%s\"}", header, message));
        }
    }
}