package com.nateyolles.sling.publick.servlets;

import org.apache.commons.lang3.StringUtils;
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
    protected void sendResponse(final PrintWriter writer, final String header, final String message) {
        sendResponse(writer, header, message, null);
    }

    /**
     * Send the JSON response.
     *
     * @param writer The PrintWriter.
     * @param header The header to send.
     * @param message The message to send.
     * @param data The data, probably JSON string
     */
    protected void sendResponse(final PrintWriter writer, final String header, final String message, final String data) {
        try {
            JSONObject json = new JSONObject();

            json.put("header", header);
            json.put("message", message);

            if (StringUtils.isNotBlank(data)) {
                json.put("data", data);
            }

            writer.write(json.toString());
        } catch (JSONException e) {
            LOGGER.error("Could not write JSON", e);

            if (StringUtils.isNotBlank(data)) {
                writer.write(String.format("{\"header\" : \"%s\", \"message\" : \"%s\", \"data\" :  \"%s\"}", header, message, data));
            } else {
                writer.write(String.format("{\"header\" : \"%s\", \"message\" : \"%s\"}", header, message));
            }
        }
    }
}