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
package org.sakaiproject.kaltura.utils.common;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.services.AuthCodeService;

/**
 * Utility class for security-specific functionality
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class SecurityUtil {

    /**
     * Is the current user an administrator in the system?
     * 
     * @param developerHelperService
     * @return true, if the current user is admin
     */
    public static boolean isAdmin() {
        DeveloperHelperService developerHelperService = (DeveloperHelperService) ComponentManager.get(DeveloperHelperService.class);
        String userReference = developerHelperService.getCurrentUserReference();

        return developerHelperService.isUserAdmin(userReference);
    }

    /**
     * Convenience method to check if a user is admin and thus has access to the service
     */
    public static void securityCheck() {
        isAdmin();
    }

    /**
     * Performs a check to ensure the authorization code matches the user ID associated with it and is not expired
     * If the authorization code is invalid, a SecurityException is thrown and access is denied
     * 
     * @param developerHelperService
     * @exception SecurityException
     */
    public static void securityCheck(String authorizationCode, String userId) {
        DeveloperHelperService developerHelperService = (DeveloperHelperService) ComponentManager.get(DeveloperHelperService.class);
        developerHelperService.restoreCurrentUser();

        if (!isAdmin()) {
            // not admin, check authorization code and user ID are valid
            AuthCodeService authCodeService = (AuthCodeService) ComponentManager.get(AuthCodeService.class);
            boolean isValid = false;

            try {
                isValid = authCodeService.isValid(authorizationCode, userId);
            } catch (Exception e) {
            }

            if (!isValid) {
                ServerConfigurationService serverConfigurationService = (ServerConfigurationService) ComponentManager.get(ServerConfigurationService.class);
                String authCodeOverride = serverConfigurationService.getString("kaltura.authorization.override.code", Constants.AUTHORIZATION_OVERRIDE_CODE);
                if (!StringUtils.equalsIgnoreCase(authorizationCode, authCodeOverride)) {
                    // the authorization code is invalid and the override does not match, don't allow access
                    throw new SecurityException("This endpoint is not accessible to non-administrators and this user is not an administrator or the auth_code is incorrect.");
                }
            }
            developerHelperService.setCurrentUser("/user/admin");
        }
    }

}
