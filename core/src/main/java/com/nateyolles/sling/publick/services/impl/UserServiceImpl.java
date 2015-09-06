package com.nateyolles.sling.publick.services.impl;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.UserService;

/**
 * Access information about users and groups.
 */
@Service( value = UserService.class )
@Component( metatype = true, immediate = true )
public class UserServiceImpl implements UserService {

    /** The logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * Get the authorable status of the current user.
     *
     * @param session The current session.
     * @return true if the current user is an admin or author.
     */
    public boolean isAuthorable(Session session) {
        boolean authorable = false;

        JackrabbitSession js = (JackrabbitSession)session;

        try {
            Group authors = (Group)js.getUserManager().getAuthorizable(PublickConstants.GROUP_ID_AUTHORS);
            User user = (User)js.getUserManager().getAuthorizable(js.getUserID());

            authorable = user.isAdmin() || authors.isMember(user);
        } catch (RepositoryException e) {
            LOGGER.error("Could not determine group membership", e);
        }

        return authorable;
    }
}