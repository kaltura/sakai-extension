/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.kaltura.Constants;

public class SecurityService {

    private final Log log = LogFactory.getLog(SecurityService.class);

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
                String authCodeOverride = serverConfigurationService.getString("kaltura.authorization.override.code", Constants.AUTHORIZATION_OVERRIDE_CODE);
                if (!StringUtils.equalsIgnoreCase(authorizationCode, authCodeOverride)) {
                    // the authorization code is invalid and the override does not match, don't allow access
                    throw new SecurityException("This endpoint is not accessible to non-administrators and this user is not an administrator or the authorization code is incorrect / has expired.");
                }
            }
            developerHelperService.setCurrentUser("/user/admin");
        }
    }

}
