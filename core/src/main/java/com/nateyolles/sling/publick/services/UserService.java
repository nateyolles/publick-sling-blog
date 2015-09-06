package com.nateyolles.sling.publick.services;

import javax.jcr.Session;

/**
 * API to access information about users and groups.
 */
public interface UserService {

    /**
     * Get the authorable status of the current user.
     *
     * @param session The current session.
     * @return true if the current user is an admin or author.
     */
    boolean isAuthorable(Session session);
}