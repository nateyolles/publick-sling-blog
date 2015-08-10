package com.nateyolles.sling.publick.components.foundation;

import org.apache.sling.api.SlingConstants;
import javax.jcr.Session;
import javax.script.Bindings;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sightly component to display the error code, message, and stack trace.
 * Display the stack trace only if the user isn't authenticated.
 */
public class ErrorHandler implements Use {

    /**
     * Logger instance to log and debug errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    private int code;
    private boolean isAnonymous = true;
    private String message;
    private String stackTrace;

    @Override
    public void init(Bindings bindings) {
        Resource resource = (Resource)bindings.get(SlingBindings.RESOURCE);
        SlingHttpServletRequest request = (SlingHttpServletRequest)bindings.get(SlingBindings.REQUEST);
        SlingHttpServletResponse response = (SlingHttpServletResponse)bindings.get(SlingBindings.RESPONSE);
        ResourceResolver resolver = resource.getResourceResolver();

        isAnonymous = "anonymous".equals(resolver.adaptTo(Session.class).getUserID());

        message = (String) request.getAttribute(SlingConstants.ERROR_MESSAGE);
        Integer scObject = (Integer) request.getAttribute(SlingConstants.ERROR_STATUS);

        code = (scObject != null) ? scObject.intValue() : response.SC_INTERNAL_SERVER_ERROR;

        if (message == null) {
            message = statusToString(code);
        }

        // Print stack trace only if the user is not anonymous
        if (!isAnonymous) {
            if (request.getAttribute(SlingConstants.ERROR_EXCEPTION) instanceof Throwable) {
                Throwable throwable = (Throwable) request.getAttribute(SlingConstants.ERROR_EXCEPTION);
                printStackTrace(throwable);
            }
        }

        response.setStatus(code);
        response.setContentType("text/html"); 
        response.setCharacterEncoding("utf-8");
    }

    /**
     * Print the stack trace for the root exception if the throwable is a
     * {@link ServletException}. If this does not contain an exception,
     * the throwable itself is printed.
     *
     * @param t The Throwable object containing the stack trace.
     */
    private void printStackTrace(Throwable t) {
        // nothing to do, if there is no exception
        if (t == null) {
            return;
        }

        // unpack a servlet exception
        if (t instanceof ServletException) {
            ServletException se = (ServletException) t;
            while (se.getRootCause() != null) {
                t = se.getRootCause();
                if (t instanceof ServletException) {
                    se = (ServletException) t;
                } else {
                    break;
                }
            }
        }

        stackTrace = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t);
    }

    /**
     * 
     * @param statusCode The status code number.
     *
     * @return The status code message.
     */
    public static String statusToString(int statusCode) {
        switch (statusCode) {
            case 100:
                return "Continue";
            case 101:
                return "Switching Protocols";
            case 102:
                return "Processing (WebDAV)";
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 202:
                return "Accepted";
            case 203:
                return "Non-Authoritative Information";
            case 204:
                return "No Content";
            case 205:
                return "Reset Content";
            case 206:
                return "Partial Content";
            case 207:
                return "Multi-Status (WebDAV)";
            case 300:
                return "Multiple Choices";
            case 301:
                return "Moved Permanently";
            case 302:
                return "Found";
            case 303:
                return "See Other";
            case 304:
                return "Not Modified";
            case 305:
                return "Use Proxy";
            case 307:
                return "Temporary Redirect";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 402:
                return "Payment Required";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case 406:
                return "Not Acceptable";
            case 407:
                return "Proxy Authentication Required";
            case 408:
                return "Request Time-out";
            case 409:
                return "Conflict";
            case 410:
                return "Gone";
            case 411:
                return "Length Required";
            case 412:
                return "Precondition Failed";
            case 413:
                return "Request Entity Too Large";
            case 414:
                return "Request-URI Too Large";
            case 415:
                return "Unsupported Media Type";
            case 416:
                return "Requested range not satisfiable";
            case 417:
                return "Expectation Failed";
            case 422:
                return "Unprocessable Entity (WebDAV)";
            case 423:
                return "Locked (WebDAV)";
            case 424:
                return "Failed Dependency (WebDAV)";
            case 500:
                return "Internal Server Error";
            case 501:
                return "Not Implemented";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            case 504:
                return "Gateway Time-out";
            case 505:
                return "HTTP Version not supported";
            case 507:
                return "Insufficient Storage (WebDAV)";
            case 510:
                return "Not Extended";
            default:
                return String.valueOf(statusCode);
        }
    }

    /**
     * Get the exception message.
     *
     * @return The exception message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the exception stack trace.
     *
     * @return The exception stack trace.
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Get the exception code.
     *
     * @return The exception code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Is the user anonymous.
     *
     * @return true if the user is not logged in.
     */
    public boolean getIsAnonymous() {
        return isAnonymous;
    }
}