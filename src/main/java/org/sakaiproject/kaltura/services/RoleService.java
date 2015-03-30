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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.Role;
import org.sakaiproject.kaltura.models.errors.ErrorRole;
import org.sakaiproject.kaltura.utils.common.JsonUtil;
import org.sakaiproject.kaltura.utils.common.RestUtil;

public class RoleService {

    public ActionReturn get(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            // no roleId specified, get all roles instead
            return getAllRoles();
        }

        ErrorRole errorRole = new ErrorRole();

        Role role = new Role();

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(role));
    }

    public ActionReturn getAllRoles() {
        ErrorRole errorRole = new ErrorRole();

        List<Role> roles = new ArrayList<Role>();

        //return processActionReturn(errorRole, JsonUtil.parseToJson(roles));
        return RestUtil.processActionReturn(errorRole, "{\"role\": \"No roles defined (yet)\"}");
    }

}
