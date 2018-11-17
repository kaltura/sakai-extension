/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Setter;

public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Setter private AuthzGroupService authzGroupService;
    @Setter private SiteService siteService;
    @Setter private EntityManager entityManager;
    @Setter private RoleService roleService;
    @Setter private UserDirectoryService userDirectoryService;

    /**
     * Get the {@link User} object associated with the given user ID
     * 
     * @param userId the Sakai internal user ID or EID
     * @return the {@link User} object
     * @throws Exception
     */
    @Override
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
    @Override
    public User getCurrentUser() {
        org.sakaiproject.user.api.User currentSakaiUser = userDirectoryService.getCurrentUser();
        if (currentSakaiUser == null) {
            return null;
        }

        return new User(currentSakaiUser);
    }

    /**
     * Populate the user's sites and role data
     * 
     * @param user the {@link User} object
     */
    @Override
    public void populateUserData(User user) {
        Set<String> userAuthzGroupIds = new HashSet<String>();
        for (String realmPermissionId : Constants.MEMBERSHIP_REALM_PERMISSION_IDS) {
            Set<String> authzGroupIds = authzGroupService.getAuthzGroupsIsAllowed(user.getId(), realmPermissionId, null);
            userAuthzGroupIds.addAll(authzGroupIds);
        }

        for (String userAuthzGroupId : userAuthzGroupIds) {
            try {
                Reference reference = entityManager.newReference(userAuthzGroupId);
                if(reference.isKnownType() && StringUtils.equalsIgnoreCase(reference.getType(), SiteService.APPLICATION_ID)) {
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
            } catch (Exception e) {
                log.error("Error retrieving AuthzGroup: " + userAuthzGroupId + " for user: " + user.getSakaiUser().getId(), e);
            }
        }
    }

}
