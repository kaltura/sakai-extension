/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services.provider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.User;
import org.sakaiproject.kaltura.models.error.ErrorUser;
import org.sakaiproject.kaltura.services.UserService;
import org.sakaiproject.kaltura.utils.JsonUtil;
import org.sakaiproject.kaltura.services.RestService;
import org.sakaiproject.kaltura.services.SecurityService;

/**
 * Service layer to support the kaltura/user entities
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class UserProviderService {

    private static final Logger log = LoggerFactory.getLogger(UserProviderService.class);

    private RestService restService;
    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    private SecurityService securityService;
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    private UserService userService;
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets the user's site and role data
     * 
     * @param userId the user's ID, if not given and user is admin, get the currently logged-in user's data
     */
    public ActionReturn get(String userId) {
        User user = null;
        ErrorUser errorUser = new ErrorUser();

        if (StringUtils.isBlank(userId) || !securityService.isAdmin()) {
            user = userService.getCurrentUser();
        } else {
            try {
                user = userService.getUser(userId);
            } catch (Exception e) {
                errorUser.updateErrorList(e.getLocalizedMessage(), "getting user by user ID", userId);
                log.error("Error getting user by user ID: " + e, e);
            }
        }

        userService.populateUserData(user);

        return restService.processActionReturn(errorUser, JsonUtil.parseToJson(user));
    }

}
