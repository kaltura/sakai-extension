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
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.api.dao.KalturaLtiRoleDao;
import org.sakaiproject.kaltura.dao.jdbc.data.RoleData;
import org.sakaiproject.kaltura.dao.models.db.KalturaLtiRole;

/**
 * A helper class for getting and calculating role objects
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class RoleService {

    private KalturaLtiRoleDao kalturaLtiRoleDao;
    public void setKalturaLtiRoleDao(KalturaLtiRoleDao kalturaLtiRoleDao) {
        this.kalturaLtiRoleDao = kalturaLtiRoleDao;
    }

    private RoleData roleData;
    public void setRoleData(RoleData roleData) {
        this.roleData = roleData;
    }

    /**
     * Get the role mapping associated with the given Sakai role
     * 
     * @param roleId the role mapping ID
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getRoleMapping(String roleId) throws Exception {
        if (StringUtils.isBlank(roleId)) {
            throw new IllegalArgumentException("Role ID cannot be blank.");
        }

        KalturaLtiRole kalturaLtiRole = kalturaLtiRoleDao.getRoleMapping(roleId);

        return kalturaLtiRole;
    }

    /**
     * Get the role mapping associated with the given Sakai role
     * 
     * @param sakaiRole the Sakai role ID
     * @return the list of {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getSakaiRoleMappings(String sakaiRole) throws Exception {
        if (StringUtils.isBlank(sakaiRole)) {
            throw new IllegalArgumentException("Sakai role cannot be blank.");
        }

        List<KalturaLtiRole> kalturaLtiRole = kalturaLtiRoleDao.getSakaiRoleMappings(sakaiRole);

        return kalturaLtiRole;
    }

    /**
     * Get the role mappings associated with the given LTI role
     * 
     * @param ltiRole the LTI role ID
     * @return the list of {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getLtiRoleMapping(String ltiRole) throws Exception {
        if (StringUtils.isBlank(ltiRole)) {
            throw new IllegalArgumentException("LTI role cannot be blank.");
        }

        List<KalturaLtiRole> kalturaLtiRoles = kalturaLtiRoleDao.getLtiRoleMappings(ltiRole);

        return kalturaLtiRoles;
    }

    /**
     * Get the role mapping associated with the given Sakai role
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getRoleMapping(String sakaiRole, String ltiRole) throws Exception {
        if (StringUtils.isBlank(sakaiRole)) {
            throw new IllegalArgumentException("Sakai role cannot be blank.");
        }
        if (StringUtils.isBlank(ltiRole)) {
            throw new IllegalArgumentException("Lti role cannot be blank.");
        }

        KalturaLtiRole kalturaLtiRole = kalturaLtiRoleDao.getRoleMapping(sakaiRole, ltiRole);

        return kalturaLtiRole;
    }

    /**
     * Get the entire list of role mappings
     * 
     * @return a list of the {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getAllRoleMappings() throws Exception {
        List<KalturaLtiRole> allRoleMappings = kalturaLtiRoleDao.getAllRoleMappings();

        return allRoleMappings;
    }

    /**
     * Adds a new role mapping from individual parameters
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @return the new {@link KalturaLtiRole} object
     */
    public KalturaLtiRole addRoleMapping(String sakaiRole, String ltiRole) throws Exception {
        if (StringUtils.isBlank(sakaiRole)) {
            throw new IllegalArgumentException("Sakai role cannot be blank.");
        }
        if (StringUtils.isBlank(ltiRole)) {
            throw new IllegalArgumentException("LTI role cannot be blank.");
        }

        KalturaLtiRole kalturaLtiRole = new KalturaLtiRole(sakaiRole, ltiRole);

        return addRoleMapping(kalturaLtiRole);
    }

    /**
     * Adds a new role mapping from a {@link KalturaLtiRole} object
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object
     * @return the new {@link KalturaLtiRole} object
     */
    public KalturaLtiRole addRoleMapping(KalturaLtiRole kalturaLtiRole) throws Exception {
        if (kalturaLtiRole == null) {
            throw new IllegalArgumentException("KalturaLtiRole cannot be null.");
        }

        kalturaLtiRoleDao.save(kalturaLtiRole);

        // get the new role mapping, with the ID
        kalturaLtiRole = getRoleMapping(kalturaLtiRole.getSakaiRole(), kalturaLtiRole.getLtiRole());

        return kalturaLtiRole;
    }

    /**
     * Updates an role mapping, if a mapping with the given ID is not found, it adds it instead
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @return the new {@link KalturaLtiRole} object
     */
    public KalturaLtiRole updateRoleMapping(String id, String sakaiRole, String ltiRole, Boolean active) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Role mapping ID cannot be blank.");
        }

        KalturaLtiRole kalturaLtiRole = new KalturaLtiRole(sakaiRole, ltiRole);

        if (StringUtils.isNotBlank(sakaiRole)) {
            kalturaLtiRole.setSakaiRole(sakaiRole);
        }
        if (StringUtils.isNotBlank(ltiRole)) {
            kalturaLtiRole.setLtiRole(ltiRole);
        }

        return updateRoleMapping(kalturaLtiRole);
    }

    /**
     * Updates a role mapping from a {@link KalturaLtiRole} object
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object
     * @return the new {@link KalturaLtiRole} object
     */
    public KalturaLtiRole updateRoleMapping(KalturaLtiRole kalturaLtiRole) throws Exception {
        if (kalturaLtiRole == null) {
            throw new IllegalArgumentException("KalturaLtiRole cannot be null.");
        }

        KalturaLtiRole toUpdate = kalturaLtiRoleDao.getRoleMapping(kalturaLtiRole.getId());
        if (toUpdate != null) {
            toUpdate.copy(kalturaLtiRole);
            kalturaLtiRoleDao.save(toUpdate);
        } else {
            // no object exists, add it instead
            toUpdate = addRoleMapping(kalturaLtiRole);
        }

        return toUpdate;
    }

    /**
     * Calculates the LTI roles based on the Sakai site role
     * 
     * @param sakaiRole the Sakai site role ID
     * @return the corresponding LTI roles (default: Learner)
     */
    public String calculateLtiRoles(String sakaiRole) throws Exception {
        String ltiRoles = "";

        List<KalturaLtiRole> kalturaLtiRoles = kalturaLtiRoleDao.getSakaiRoleMappings(sakaiRole);

        for (KalturaLtiRole kalturaLtiRole : kalturaLtiRoles) {
            ltiRoles += kalturaLtiRole.getLtiRole() + ",";
        }

        if (StringUtils.isBlank(ltiRoles)) {
            ltiRoles = Constants.DEFAULT_LTI_ROLE;
        } else {
            StringUtils.chomp(ltiRoles, ",");
        }

        return ltiRoles;
    }

    /**
     * Get a listing of all Sakai site roles in the system
     * 
     * @return the list of Sakai role IDs
     */
    public List<String> getAllSakaiRoles() throws Exception {
        List<String> allSakaiRoles = roleData.getSakaiRoles();

        return allSakaiRoles;
    }

    /**
     * Get a listing of all Sakai site roles in the system
     * 
     * @return the list of Sakai role IDs
     */
    public List<String> getAllLtiRoles() throws Exception {
        List<String> allLtiRoles = new ArrayList<String>(Constants.DEFAULT_LTI_ROLES.length);

        for (String defaultLtiRole : Constants.DEFAULT_LTI_ROLES) {
            allLtiRoles.add(defaultLtiRole);
        }

        return allLtiRoles;
    }

    /**
     * Delete a custom role mapping
     * 
     * @param id the ID of the row mapping
     */
    public void deleteRoleMapping(String id) {
        kalturaLtiRoleDao.delete(id);
    }

}
