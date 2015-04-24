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
package org.sakaiproject.kaltura.services.provider;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.User;
import org.sakaiproject.kaltura.models.errors.ErrorUser;
import org.sakaiproject.kaltura.services.UserService;
import org.sakaiproject.kaltura.utils.common.JsonUtil;
import org.sakaiproject.kaltura.utils.common.RestUtil;
import org.sakaiproject.kaltura.utils.common.SecurityUtil;

/**
 * Service layer to support the kaltura/user entities
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class UserProviderService {

    private final Log log = LogFactory.getLog(UserProviderService.class);

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

        if (StringUtils.isBlank(userId) || !SecurityUtil.isAdmin()) {
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

        return RestUtil.processActionReturn(errorUser, JsonUtil.parseToJson(user));
    }

    

}
