package com.nateyolles.sling.publick.filters;

import java.util.concurrent.LinkedBlockingDeque;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;

/**
 * Filter to throttle post requests
 * Adapted from OWASP ESAPI for Java
 */
@Component(immediate = true, metatype = true)
@Service(Filter.class)
@Properties({
    @Property(name="service.pid", value="com.nateyolles.sling.publick.filters.RequestThrottleFilter", propertyPrivate=false),
    @Property(name="service.description", value="RequestThrottleFilter", propertyPrivate=false),
    @Property(name="service.vendor", value="Publick", propertyPrivate=false),
    @Property(name="filter.scope", value="request"),
    @Property(name="sling.filter.scope", value="request"),
    @Property(name="service.ranking", intValue=100001)
})
public class RequestThrottleFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestThrottleFilter.class);
    private static final int HITS = 1;
    private static final int SECONDS = 10;
    private static final String TIMES = "requestThrottleFilter.times";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
            final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        final HttpSession session = httpServletRequest.getSession(true);
        final String path = httpServletRequest.getPathInfo().toLowerCase();
        final String method = httpServletRequest.getMethod();

        if ("POST".equals(method) && path.startsWith(PublickConstants.SERVLET_PATH_PUBLIC)) {
            synchronized (session.getId().intern()) {
                LinkedBlockingDeque<Long> times = (LinkedBlockingDeque<Long>)session.getAttribute(TIMES);

                if (times == null) {
                    times = new LinkedBlockingDeque<Long>();
                    session.setAttribute(TIMES, times);
                }

                final long currentTimeMillis = System.currentTimeMillis();

                times.push(Long.valueOf(currentTimeMillis));
                final long cutoff = currentTimeMillis - (SECONDS * 1000);
                Long oldest = times.peekLast();
                while (oldest != null && oldest.longValue() < cutoff) {
                    times.removeLast();
                    oldest =  times.peekLast();
                }

                if (times.size() > HITS) {
                    addResponseHeaderNoCache((HttpServletResponse)response);
                    response.getWriter().println("Your request hit rate is too high;"
                            + " please press back, wait a few seconds, and then try again.");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void addResponseHeaderNoCache(final HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-stale=0,"
                + " max-age=0, post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }
}