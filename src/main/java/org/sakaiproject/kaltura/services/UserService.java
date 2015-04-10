/**
 * Copyright 2014 Sakaiproject Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.sakaiproject.kaltura.services;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.User;
import org.sakaiproject.kaltura.models.UserSiteRole;
import org.sakaiproject.kaltura.models.errors.ErrorUser;
import org.sakaiproject.kaltura.utils.common.JsonUtil;
import org.sakaiproject.kaltura.utils.common.RestUtil;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * Service layer to support the auto-roster/user entities
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class UserService {

    private final Log log = LogFactory.getLog(UserService.class);

    /**
     * {@link AuthzGroupService}
     */
    private AuthzGroupService authzGroupService;
    public void setAuthzGroupService(AuthzGroupService authzGroupService) {
        this.authzGroupService = authzGroupService;
    }

    /**
     * {@link EntityManager}
     */
    private EntityManager entityManager;
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@link RoleService}
     */
    private RoleService roleService;
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * {@link UserDirectoryService}
     */
    private UserDirectoryService userDirectoryService;
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    /**
     * Gets the user's site and role data
     * 
     * @param userId the user's ID, if not given, get the currently logged-in user's data
     */
    public ActionReturn get(String userId) {
        User user = null;
        ErrorUser errorUser = new ErrorUser();

        if (StringUtils.isBlank(userId)) {
            user = new User(userDirectoryService.getCurrentUser());
        } else {
            try {
                user = new User(userDirectoryService.getUser(userId));
            } catch (UserNotDefinedException e) {
                errorUser.updateErrorList(e.getLocalizedMessage(), "getting user by user ID", userId);
                log.error("Error getting user by user ID: " + e, e);
            }
        }

        populateUserData(user);

        return RestUtil.processActionReturn(errorUser, JsonUtil.parseToJson(user));
    }

    /**
     * Populate the user's sites and role data
     * 
     * @param user the {@link User} object
     */
    private void populateUserData(User user) {
        Set<String> userAuthzGroupIds = authzGroupService.getAuthzGroupsIsAllowed(user.getId(), "site.visit", null);

        for (String userAuthzGroupId : userAuthzGroupIds) {
            try {
                Reference reference = entityManager.newReference(userAuthzGroupId);
                if(reference.isKnownType()) {
                   if(StringUtils.equalsIgnoreCase(reference.getType(), SiteService.APPLICATION_ID)) {
                       String siteId = reference.getId();
                       if (StringUtils.isNotBlank(siteId)) {
                           UserSiteRole userSiteRole = new UserSiteRole(siteId);

                           AuthzGroup authzGroup = authzGroupService.getAuthzGroup(userAuthzGroupId);
                           Role role = authzGroup.getUserRole(user.getId());
                           userSiteRole.setSiteRole(role.getId());

                           String ltiRole = roleService.calculateLtiRole(role.getId());
                           userSiteRole.setLtiRole(ltiRole);

                           user.addUserSiteRole(userSiteRole);
                       }
                   }
                }
            } catch (Exception e) {
                log.error("Error retrieving AuthzGroup: " + userAuthzGroupId + " for user: " + user.getSakaiUser().getId(), e);
            }
        }
    }

}
