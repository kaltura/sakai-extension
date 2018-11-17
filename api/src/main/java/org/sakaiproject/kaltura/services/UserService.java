package org.sakaiproject.kaltura.services;

import org.sakaiproject.kaltura.models.User;

public interface UserService {
    User getUser(String userId) throws Exception;

    User getCurrentUser();

    void populateUserData(User user);
}
