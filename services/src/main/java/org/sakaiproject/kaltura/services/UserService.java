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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.models.User;
import org.sakaiproject.kaltura.models.UserSiteRole;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

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
     * {@link SiteService}
     */
    private SiteService siteService;
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
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
     * Get the {@link User} object associated with the given user ID
     * 
     * @param userId the Sakai internal user ID or EID
     * @return the {@link User} object
     * @throws Exception
     */
    public User getUser(String userId) throws Exception {
        org.sakaiproject.user.api.User sakaiUser = null;

        try {
            // try getting user by internal id
            sakaiUser = userDirectoryService.getUser(userId);
        } catch (UserNotDefinedException unde) {
            // user not defined internally by userId given, try finding by eid
            sakaiUser = userDirectoryService.getUserByEid(userId);
        }

        if (sakaiUser == null) {
            log.error("User not ound with id/eid: " + userId);
            throw new UserNotDefinedException(userId);
        }

        return new User(sakaiUser);
    }

    /**
     * Get the {@link User} object associated with the currently logged-in user
     * 
     * @return the {@link User} object
     */
    public User getCurrentUser() {
        return new User(userDirectoryService.getCurrentUser());
    }

    /**
     * Populate the user's sites and role data
     * 
     * @param user the {@link User} object
     */
    public void populateUserData(User user) {
        Set<String> userAuthzGroupIds = new HashSet<String>();
        for (String realmPermissionId : Constants.MEMBERSHIP_REALM_PERMISSION_IDS) {
            Set<String> authzGroupIds = authzGroupService.getAuthzGroupsIsAllowed(user.getId(), realmPermissionId, null);
            userAuthzGroupIds.addAll(authzGroupIds);
        }

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
                           String ltiRole = roleService.calculateLtiRoles(role.getId());
                           userSiteRole.setLtiRoles(ltiRole);

                           Site site = siteService.getSite(siteId);
                           if (site != null) {
                               String siteTitle = site.getTitle();
                               userSiteRole.setContextTitle(siteTitle);
                           }
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
