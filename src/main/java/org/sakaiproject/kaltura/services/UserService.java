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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.User;
import org.sakaiproject.kaltura.models.errors.ErrorUser;
import org.sakaiproject.kaltura.utils.common.JsonUtil;
import org.sakaiproject.kaltura.utils.common.RestUtil;
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
     * {@link UserDirectoryService}
     */
    protected UserDirectoryService userDirectoryService;
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

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

        return RestUtil.processActionReturn(errorUser, JsonUtil.parseToJson(user));
    }

}
