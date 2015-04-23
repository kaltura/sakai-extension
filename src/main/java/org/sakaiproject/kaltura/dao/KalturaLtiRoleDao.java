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
package org.sakaiproject.kaltura.dao;

import java.util.List;

import org.sakaiproject.genericdao.api.GeneralGenericDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;

/**
 * DAO Interface for Kaltura custom role mappings
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public interface KalturaLtiRoleDao extends GeneralGenericDao {

    /**
     * Get the entire list of role mappings
     * 
     * @return list of {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getAllRoleMappings();

    /**
     * Get a listing of the active role mappings
     * 
     * @return list of active {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getActiveRoleMappings();

    /**
     * Get a listing of the inactive role mappings
     * 
     * @return list of inactive {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getInactiveRoleMappings();

    /**
     * Get the role mapping associated with the role mapping ID
     * 
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getRoleMapping(long roleMappingId);

    /**
     * Get the role mapping associated with the role mapping ID
     * 
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getRoleMapping(String roleMappingId);

    /**
     * Get the role mapping associated with the given Sakai role
     * 
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getSakaiRoleMapping(String sakaiRole);

    /**
     * Get the role mappings associated with the given LTI role
     * 
     * @return the list of {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getLtiRoleMappings(String ltiRole);

    /**
     * Add/update a new role mapping
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object to add
     * 
     * @return true, if added/updated successfully
     */
    public boolean persistRoleMapping(KalturaLtiRole kalturaLtiRole);

    /**
     * Delete a role mapping
     * Note: this is a soft delete, only marks the "active" column as false
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object to mark as inactive
     * 
     * @return true, if marked inactive
     */
    public boolean deleteRoleMapping(KalturaLtiRole kalturaLtiRole);

    public void commit();

}
