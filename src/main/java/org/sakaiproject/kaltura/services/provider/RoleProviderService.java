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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;
import org.sakaiproject.kaltura.models.errors.ErrorRole;
import org.sakaiproject.kaltura.services.RoleService;
import org.sakaiproject.kaltura.utils.common.JsonUtil;
import org.sakaiproject.kaltura.utils.common.RestUtil;

public class RoleProviderService {

    private final Log log = LogFactory.getLog(RoleProviderService.class);

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
     * @param roleId the role mapping ID
     */
    public ActionReturn get(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            // no role ID specified, get all roles instead
            return getAllRoles();
        }

        ErrorRole errorRole = new ErrorRole();

        KalturaLtiRole kalturaLtiRole = null;

        try {
            kalturaLtiRole = roleService.getRoleMapping(roleId);
        } catch (Exception e) {
            errorRole.updateErrorList(e.toString(), "get", kalturaLtiRole.toString());
            log.error(e.toString(), e);
        }

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(kalturaLtiRole));
    }

    /**
     * Gets all Sakai role : LTI role mapping data
     */
    public ActionReturn getAllRoles() {
        ErrorRole errorRole = new ErrorRole();

        List<KalturaLtiRole> allKalturaLtiRoleMappings = new ArrayList<KalturaLtiRole>();

        try {
            allKalturaLtiRoleMappings = roleService.getAllRoleMappings();
        } catch (Exception e) {
            errorRole.updateErrorList(e.toString(), "get", null);
            log.error(e.toString(), e);
        }

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(allKalturaLtiRoleMappings));
    }

    /**
     * Adds a new role mapping
     * 
     * @param data the JSON string containing the data for the new mapping
     */
    public ActionReturn addRoleMapping(String data) {
        ErrorRole errorRole = new ErrorRole();

        List<Object> kalturaLtiRoles = JsonUtil.parseFromJson(data, KalturaLtiRole.class);

        for (Object k : kalturaLtiRoles) {
            if (!(k instanceof KalturaLtiRole)) {
                continue;
            }

            KalturaLtiRole kalturaLtiRole = (KalturaLtiRole) k;

            try {
                roleService.addRoleMapping(kalturaLtiRole);
            } catch (Exception e) {
                errorRole.updateErrorList(e.toString(), "add", kalturaLtiRole.toString());
                log.error(e.toString(), e);
            }

        }

        return RestUtil.processActionReturn(errorRole);
    }

    /**
     * Updates a role mapping
     * 
     * @param data the JSON string containing the data for the mapping
     */
    public ActionReturn updateRoleMapping(String id, String data) {
        ErrorRole errorRole = new ErrorRole();

        List<Object> kalturaLtiRoles = JsonUtil.parseFromJson(data, KalturaLtiRole.class);

        for (Object k : kalturaLtiRoles) {
            if (!(k instanceof KalturaLtiRole)) {
                continue;
            }

            KalturaLtiRole kalturaLtiRole = (KalturaLtiRole) k;
            kalturaLtiRole.setId(Long.parseLong(id));

            try {
                roleService.updateRoleMapping(kalturaLtiRole);
            } catch (Exception e) {
                errorRole.updateErrorList(e.toString(), "update", kalturaLtiRole.toString());
            }
        }

        return RestUtil.processActionReturn(errorRole);
    }

    /**
     * Gets all Sakai roles defined
     */
    public ActionReturn getAllSakaiRoles() {
        ErrorRole errorRole = new ErrorRole();

        List<String> allSakaiRoles = new ArrayList<String>();

        try {
            allSakaiRoles = roleService.getAllSakaiRoles();
        } catch (Exception e) {
            errorRole.updateErrorList(e.toString(), "get", null);
            log.error(e.toString(), e);
        }

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(allSakaiRoles));
    }

    /**
     * Gets all Sakai roles defined
     */
    public ActionReturn getAllLtiRoles() {
        ErrorRole errorRole = new ErrorRole();

        List<String> allLtiRoles = new ArrayList<String>();

        try {
            allLtiRoles = roleService.getAllLtiRoles();
        } catch (Exception e) {
            errorRole.updateErrorList(e.toString(), "get", null);
            log.error(e.toString(), e);
        }

        return RestUtil.processActionReturn(errorRole, JsonUtil.parseToJson(allLtiRoles));
    }

}
