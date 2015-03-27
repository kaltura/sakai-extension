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
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;

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
     * Performs a check to ensure the user accessing the app is an administrator in the system.
     * If the user is NOT an administrator, a SecurityException is thrown and access is denied
     * 
     * @param developerHelperService
     * @exception SecurityException
     */
    public static void securityCheck() {
        securityCheck(null);
    }

    /**
     * Performs a check to ensure the user accessing the app is an administrator in the system.
     * If there is a shared_secret key present, allow the admin user to be set
     * If the user is NOT an administrator, a SecurityException is thrown and access is denied
     * 
     * @param developerHelperService
     * @param sharedSecret the security code for session creation
     * @exception SecurityException
     */
    public static void securityCheck(String sharedSecret) {
        DeveloperHelperService developerHelperService = (DeveloperHelperService) ComponentManager.get(DeveloperHelperService.class);
        developerHelperService.restoreCurrentUser();
        if (!isAdmin()) {
            // not admin, check shared_secret
            String secretKey = developerHelperService.getConfigurationSetting("autoroster.shared_secret", "c48cb080-852b-11e4-80c2-0002a5d5c51b");
            if (!StringUtils.equals(sharedSecret, secretKey)) {
                // shared_secret key is incorrect, don't allow access
                throw new SecurityException("This endpoint is not accessible to non-administrators and this user is not an administrator or the shared_secret is incorrect.");
            }
            developerHelperService.setCurrentUser("/user/admin");
        }
    }

}
