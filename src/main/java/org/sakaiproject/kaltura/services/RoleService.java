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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Saveable;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.dao.KalturaLtiRoleDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;

/**
 * A helper class for getting and calculating role objects
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class RoleService {

    private final Log log = LogFactory.getLog(RoleService.class);

    private KalturaLtiRoleDao kalturaLtiRoleDao;
    public void setKalturaLtiRoleDao(KalturaLtiRoleDao kalturaLtiRoleDao) {
        this.kalturaLtiRoleDao = kalturaLtiRoleDao;
    }

    /**
     * Get the role mapping associated with the given Sakai role
     * 
     * @param roleId the role mapping ID
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getRoleMapping(String roleId) {
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
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getSakaiRoleMapping(String sakaiRole) {
        if (StringUtils.isBlank(sakaiRole)) {
            throw new IllegalArgumentException("Sakai role cannot be blank.");
        }

        KalturaLtiRole kalturaLtiRole = kalturaLtiRoleDao.getSakaiRoleMapping(sakaiRole);

        return kalturaLtiRole;
    }

    /**
     * Get the role mappings associated with the given LTI role
     * 
     * @param ltiRole the LTI role ID
     * @return the list of {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getLtiRoleMapping(String ltiRole) {
        if (StringUtils.isBlank(ltiRole)) {
            throw new IllegalArgumentException("LTI role cannot be blank.");
        }

        List<KalturaLtiRole> kalturaLtiRoles = kalturaLtiRoleDao.getLtiRoleMappings(ltiRole);

        return kalturaLtiRoles;
    }

    /**
     * Get the entire list of role mappings
     * 
     * @return a list of the {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getAllRoleMappings() {
        List<KalturaLtiRole> allRoleMappings = kalturaLtiRoleDao.getAllRoleMappings();

        return allRoleMappings;
    }

    /**
     * Get the list of active role mappings
     * 
     * @return a list of the {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getActiveRoleMappings() {
        List<KalturaLtiRole> activeRoleMappings = kalturaLtiRoleDao.getActiveRoleMappings();

        return activeRoleMappings;
    }

    /**
     * Get the list of inactive role mappings
     * 
     * @return a list of the {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getInactiveRoleMappings() {
        List<KalturaLtiRole> inactiveRoleMappings = kalturaLtiRoleDao.getInactiveRoleMappings();

        return inactiveRoleMappings;
    }

    /**
     * Adds a new role mapping from individual parameters
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @param active is this an active mapping?
     * @return the new {@link KalturaLtiRole} object
     */
    public KalturaLtiRole addRoleMapping(String sakaiRole, String ltiRole, Boolean active) {
        if (StringUtils.isBlank(sakaiRole)) {
            throw new IllegalArgumentException("Sakai role cannot be blank.");
        }
        if (StringUtils.isBlank(ltiRole)) {
            throw new IllegalArgumentException("LTI role cannot be blank.");
        }
        if (active == null) {
            active = Constants.DEFAULT_ROLE_MAPPING_ACTIVE;
        }

        KalturaLtiRole kalturaLtiRole = new KalturaLtiRole(sakaiRole, ltiRole, active);

        return addRoleMapping(kalturaLtiRole);
    }

    /**
     * Adds a new role mapping from a {@link KalturaLtiRole} object
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object
     * @return the new {@link KalturaLtiRole} object
     */
    public KalturaLtiRole addRoleMapping(KalturaLtiRole kalturaLtiRole) {
        if (kalturaLtiRole == null) {
            throw new IllegalArgumentException("KalturaLtiRole cannot be null.");
        }

        kalturaLtiRoleDao.save(kalturaLtiRole);

        return kalturaLtiRole;
    }

    /**
     * Updates an role mapping, if a mapping with the given ID is not found, it adds it instead
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @param active is this an active mapping?
     * @return the new {@link KalturaLtiRole} object
     */
    public KalturaLtiRole updateRoleMapping(String id, String sakaiRole, String ltiRole, Boolean active) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Role mapping ID cannot be blank.");
        }

        KalturaLtiRole kalturaLtiRole = kalturaLtiRoleDao.getRoleMapping(id);

        if (kalturaLtiRole == null) {
            return addRoleMapping(sakaiRole, ltiRole, active);
        }

        if (StringUtils.isNotBlank(sakaiRole)) {
            kalturaLtiRole.setSakaiRole(sakaiRole);
        }
        if (StringUtils.isNotBlank(ltiRole)) {
            kalturaLtiRole.setLtiRole(ltiRole);
        }
        if (active != null) {
            kalturaLtiRole.setActive(active);
        }

        return updateRoleMapping(kalturaLtiRole);
    }

    /**
     * Updates a role mapping from a {@link KalturaLtiRole} object
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object
     * @return the new {@link KalturaLtiRole} object
     */
    public KalturaLtiRole updateRoleMapping(KalturaLtiRole kalturaLtiRole) {
        if (kalturaLtiRole == null) {
            throw new IllegalArgumentException("KalturaLtiRole cannot be null.");
        }

        if (checkRoleMappingExists(kalturaLtiRole.getId())) {
            kalturaLtiRoleDao.save(kalturaLtiRole);
        } else {
            throw new IllegalArgumentException("No role mapping exists with the given ID: " + kalturaLtiRole.getId());
        }

        return kalturaLtiRole;
    }

    /**
     * Checks whether or not a role mapping exists with the given ID
     * 
     * @param id the ID of the role mapping
     * @return true, if it exists
     */
    public boolean checkRoleMappingExists(String id) {
        KalturaLtiRole existingKalturaLtiRole = kalturaLtiRoleDao.getRoleMapping(id);

        return existingKalturaLtiRole != null;
    }

    /**
     * Checks whether or not a role mapping exists with the given ID
     * 
     * @param id the ID of the role mapping
     * @return true, if it exists
     */
    public boolean checkRoleMappingExists(long id) {
        KalturaLtiRole existingKalturaLtiRole = kalturaLtiRoleDao.getRoleMapping(id);

        return existingKalturaLtiRole != null;
    }

    /**
     * Calculates the LTI role based on the Sakai site role
     * 
     * @param sakaiRole the Sakai site role ID
     * @return the corresponding LTI role (default: Learner)
     */
    public String calculateLtiRole(String sakaiRole) {
        String ltiRole = Constants.DEFAULT_LTI_ROLE;

        KalturaLtiRole kalturaLtiRole = kalturaLtiRoleDao.getSakaiRoleMapping(sakaiRole);

        if (kalturaLtiRole != null) {
            ltiRole = kalturaLtiRole.getLtiRole();
        }

        return ltiRole;
    }

}
