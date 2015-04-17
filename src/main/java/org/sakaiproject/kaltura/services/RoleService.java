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
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.dao.KalturaLtiRoleDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;

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
     * Get the role mapping associated with the given LTI role
     * 
     * @param ltiRole the LTI role ID
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getLtiRoleMapping(String ltiRole) {
        if (StringUtils.isBlank(ltiRole)) {
            throw new IllegalArgumentException("LTI role cannot be blank.");
        }

        KalturaLtiRole kalturaLtiRole = kalturaLtiRoleDao.getLtiRoleMapping(ltiRole);

        return kalturaLtiRole;
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
