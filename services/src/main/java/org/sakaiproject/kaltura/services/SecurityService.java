/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityService {

    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);

    private AuthCodeService authCodeService;
    public void setAuthCodeService(AuthCodeService authCodeService) {
        this.authCodeService = authCodeService;
    }

    private DeveloperHelperService developerHelperService;
    public void setDeveloperHelperService(
            DeveloperHelperService developerHelperService) {
        this.developerHelperService = developerHelperService;
    }

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(
            ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    private SiteService siteService;
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * Is the current user an administrator in the system?
     * 
     * @param developerHelperService
     * @return true, if the current user is admin
     */
    public boolean isAdmin() {
        String userReference = developerHelperService.getCurrentUserReference();

        return developerHelperService.isUserAdmin(userReference);
    }

    /**
     * Convenience method to check if a user is admin and thus has access to the service
     */
    public void securityCheck() {
        isAdmin();
    }

    /**
     * Performs a check to ensure the authorization code matches the user ID associated with it and is not expired
     * If the authorization code is invalid, a SecurityException is thrown and access is denied
     * 
     * @param developerHelperService
     * @exception SecurityException
     */
    public void securityCheck(String authorizationCode, String userId) {
        developerHelperService.restoreCurrentUser();

        if (!isAdmin()) {
            // not admin, check authorization code and user ID are valid
            boolean isValid = false;

            try {
                isValid = authCodeService.isValid(authorizationCode, userId);
            } catch (Exception e) {
                log.error("There was an error validating the authorization code: " + authorizationCode + " for user: " + userId, e);
            }

            if (!isValid) {
                // if authorization is not valid lets try the override code
                String authCodeOverride = serverConfigurationService.getString("kaltura.authorization.override.code");
                if (StringUtils.isNotBlank(authCodeOverride) && StringUtils.equalsIgnoreCase(authorizationCode, authCodeOverride)) {
                    isValid = true;
                }
            }

            if (isValid) {
                developerHelperService.setCurrentUser("/user/admin");
            } else {
                // the authorization code is invalid and the override does not match, don't allow access
                throw new SecurityException("This endpoint is not accessible to non-administrators and this user is not an administrator or the authorization code is incorrect / has expired.");
            }
        }
    }

    /**
     * Is the current user allowed access to the site with the given ID?
     * 
     * @param the site's ID
     * @return true, if the current user can access the site
     */
    public boolean isAllowedAccess(String siteId) {
        if (StringUtils.isBlank(siteId)) {
            throw new IllegalArgumentException("Site ID cannot be null");
        }

        String currentUserRef = developerHelperService.getCurrentUserReference();
        if (StringUtils.isBlank(currentUserRef)) {
            throw new IllegalArgumentException("There is no currently defined user.");
        }

        if (StringUtils.startsWith(siteId,"~")) {
            siteId = "~" + StringUtils.split(currentUserRef, '/')[1];
        }

        Site site;
        try {
            site = siteService.getSite(siteId);
        } catch (IdUnusedException e) {
            log.error("There is no site defined with ID: " + siteId);
            return false;
        }

        return isAllowedAccess(currentUserRef, site.getReference());
    }

    /**
     * Is the user allowed access into the site with the given ID?
     * 
     * @param userRef the user's ref (/user/{user_id}
     * @param siteId the site's ref (/site/{site_id})
     * @return true, if the user can access the site
     */
    public boolean isAllowedAccess(String userRef, String siteRef) {
        if (StringUtils.isBlank(userRef)) {
            throw new IllegalArgumentException("User ref ID cannot be null");
        }
        if (StringUtils.isBlank(siteRef)) {
            throw new IllegalArgumentException("Site ref ID cannot be null");
        }

        return developerHelperService.isUserAllowedInEntityReference(userRef, SiteService.SITE_VISIT, siteRef);
    }

}
