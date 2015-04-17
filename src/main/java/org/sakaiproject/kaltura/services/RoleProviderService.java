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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;
import org.sakaiproject.kaltura.models.errors.ErrorRole;
import org.sakaiproject.kaltura.utils.common.JsonUtil;
import org.sakaiproject.kaltura.utils.common.RestUtil;

public class RoleProviderService {

    private RoleService roleService;
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public void init() {
    }

    /**
     * Gets the role data for the given Sakai Role ID
     * If no ID is given, get all LTI role data
     * 
     * @param sakaiRole the Sakai role ID
     */
    public ActionReturn get(String sakaiRole) {
        if (StringUtils.isBlank(sakaiRole)) {
            // no Sakai role specified, get all roles instead
            return getAllRoles();
        }

        ErrorRole errorRole = new ErrorRole();

        KalturaLtiRole kalturaLtiRole = roleService.getSakaiRoleMapping(sakaiRole);

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(kalturaLtiRole));
    }

    /**
     * Gets all Sakai role : LTI role mapping data
     * 
     * @return
     */
    public ActionReturn getAllRoles() {
        ErrorRole errorRole = new ErrorRole();

        List<KalturaLtiRole> allKalturaLtiRoleMappings = roleService.getAllRoleMappings();

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(allKalturaLtiRoleMappings));
    }

}
