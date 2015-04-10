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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.Role;
import org.sakaiproject.kaltura.models.errors.ErrorRole;
import org.sakaiproject.kaltura.utils.common.JsonUtil;
import org.sakaiproject.kaltura.utils.common.RestUtil;

public class RoleService {

    private List<Role> roleMapping;

    public void init() {
        initRoleMapping();
    }

    /**
     * Gets the role data for the given Sakai Role ID
     * If no ID is given, get all LTI role data
     * 
     * @param sakaiRoleId the Sakai role ID
     */
    public ActionReturn get(String sakaiRoleId) {
        if (StringUtils.isBlank(sakaiRoleId)) {
            // no roleId specified, get all roles instead
            return getAllRoles();
        }

        ErrorRole errorRole = new ErrorRole();

        Role role = new Role();
        for (Role roleMap : roleMapping) {
            if (StringUtils.equalsIgnoreCase(roleMap.getSakaiRoleId(), sakaiRoleId)) {
                role = new Role(roleMap.getSakaiRoleId(), roleMap.getLtiRoleId());
            }
        }

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(role));
    }

    /**
     * Gets all Sakai role : LTI role mapping data
     * 
     * @return
     */
    public ActionReturn getAllRoles() {
        ErrorRole errorRole = new ErrorRole();

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(roleMapping));
    }

    /**
     * Creates the initial role mapping and stores it in a cache
     */
    public void initRoleMapping() {
        roleMapping = new ArrayList<Role>();

        // TODO create a non-expiring cache for this
        // TODO persist in db
        // TODO this is TEMPORARY
        Map<String, String> roles = new HashMap<String, String>();
        roles.put("Instructor", "Instructor");
        roles.put("maintain", "Instructor");
        roles.put("Student", "Learner");
        roles.put("access", "Learner");

        for (String sakaiRole : roles.keySet()) {
            roleMapping.add(new Role(sakaiRole, roles.get(sakaiRole)));
        }
    }

    /**
     * Calculates the LTI role based on the Sakai site role
     * 
     * @param siteRoleId the Sakai site role ID
     * @return the corresponding LTI role (default: Learner)
     */
    public String calculateLtiRole(String siteRoleId) {
        // default to "Learner"
        String ltiRole = "Learner";

        if (roleMapping != null) {
            for (Role roleMap : roleMapping) {
                if (StringUtils.equalsIgnoreCase(roleMap.getSakaiRoleId(), siteRoleId)) {
                    ltiRole = roleMap.getLtiRoleId();
                    break;
                }
            }
        }

        return ltiRole;
    }

}
