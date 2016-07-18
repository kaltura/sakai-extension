/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.error.ErrorRole;
import org.sakaiproject.kaltura.models.dao.KalturaLtiRole;
import org.sakaiproject.kaltura.services.RestService;
import org.sakaiproject.kaltura.services.RoleService;
import org.sakaiproject.kaltura.utils.JsonUtil;
import org.sakaiproject.kaltura.utils.RoleUtil;

/**
 * Service layer to support the kaltura/role entities
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class RoleProviderService {

    private static final Logger log = LoggerFactory.getLogger(RoleProviderService.class);

    private RestService restService;
    public void setRestService(RestService restService) {
        this.restService = restService;
    }

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

        return restService.processActionReturn(errorRole, JsonUtil.parseToJson(kalturaLtiRole));
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

        return restService.processActionReturn(errorRole, JsonUtil.parseToJson(allKalturaLtiRoleMappings));
    }

    /**
     * Adds a new role mapping
     * 
     * @param data the JSON string containing the data for the new mapping
     */
    public ActionReturn addRoleMapping(String data) {
        ErrorRole errorRole = new ErrorRole();

        List<Object> kalturaLtiRoles = JsonUtil.parseFromJson(data, KalturaLtiRole.class);
        List<KalturaLtiRole> added = new ArrayList<KalturaLtiRole>(kalturaLtiRoles.size());

        for (Object k : kalturaLtiRoles) {
            if (!(k instanceof KalturaLtiRole)) {
                continue;
            }

            KalturaLtiRole kalturaLtiRole = (KalturaLtiRole) k;

            if (!RoleUtil.isValidSakaiRoleId(kalturaLtiRole.getSakaiRole())) {
                String msg = "Invalid Sakai role ID: " + kalturaLtiRole.getSakaiRole();
                errorRole.updateErrorList(msg, "add", kalturaLtiRole.toString());
                log.error(msg);

                continue;
            }

            if (!RoleUtil.isValidLtiRoleId(kalturaLtiRole.getLtiRole())) {
                String msg = "Invalid LTI role ID: " + kalturaLtiRole.getLtiRole();
                errorRole.updateErrorList(msg, "add", kalturaLtiRole.toString());
                log.error(msg);

                continue;
            }

            try {
                kalturaLtiRole = roleService.addRoleMapping(kalturaLtiRole);
                added.add(kalturaLtiRole);
            } catch (Exception e) {
                errorRole.updateErrorList(e.toString(), "add", kalturaLtiRole.toString());
                log.error(e.toString(), e);

                continue;
            }

        }

        return restService.processActionReturn(errorRole, JsonUtil.parseToJson(added));
    }

    /**
     * Updates a role mapping
     * 
     * @param data the JSON string containing the data for the mapping
     */
    public ActionReturn updateRoleMapping(String id, String data) {
        ErrorRole errorRole = new ErrorRole();

        List<Object> kalturaLtiRoles = JsonUtil.parseFromJson(data, KalturaLtiRole.class);
        List<KalturaLtiRole> updated = new ArrayList<KalturaLtiRole>(kalturaLtiRoles.size());

        for (Object k : kalturaLtiRoles) {
            if (!(k instanceof KalturaLtiRole)) {
                continue;
            }

            KalturaLtiRole kalturaLtiRole = (KalturaLtiRole) k;
            kalturaLtiRole.setId(Long.parseLong(id));

            try {
                kalturaLtiRole = roleService.updateRoleMapping(kalturaLtiRole);
                updated.add(kalturaLtiRole);
            } catch (Exception e) {
                errorRole.updateErrorList(e.toString(), "update", kalturaLtiRole.toString());
            }
        }

        return restService.processActionReturn(errorRole, JsonUtil.parseToJson(updated));
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

        return restService.processActionReturn(errorRole, JsonUtil.parseToJson(allSakaiRoles));
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

        return restService.processActionReturn(errorRole, JsonUtil.parseToJson(allLtiRoles));
    }

    /**
     * Deletes a role mapping
     * 
     * @param id the ID of the role mapping
     */
    public ActionReturn deleteRoleMapping(String id) {
        ErrorRole errorRole = new ErrorRole();

        try {
            roleService.deleteRoleMapping(id);
        } catch (Exception e) {
            errorRole.updateErrorList(e.toString(), "delete", null);
            log.error(e.toString(), e);
        }

        return restService.processActionReturn(errorRole);
    }

}
