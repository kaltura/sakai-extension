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
package org.sakaiproject.kaltura.api.dao;

import java.util.List;

import org.hibernate.Transaction;
import org.sakaiproject.genericdao.api.GeneralGenericDao;
import org.sakaiproject.kaltura.models.dao.KalturaLtiRole;

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
     * Get the role mapping associated with the role mapping ID
     * 
     * @param roleMappingId the role mapping ID
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getRoleMapping(long roleMappingId);

    /**
     * Get the role mapping associated with the role mapping ID
     * 
     * @param roleMappingId the role mapping ID
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole getRoleMapping(String roleMappingId);

    /**
     * Get the role mappings associated with the given Sakai role
     * 
     * @param sakaiRole the Sakai role ID
     * @return the list of {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getSakaiRoleMappings(String sakaiRole);

    /**
     * Get the role mappings associated with the given Sakai and LTI roles
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @return the list of {@link KalturaLtiRole} objects
     */
    public KalturaLtiRole getRoleMapping(String sakaiRole, String ltiRole);

    /**
     * Get the role mappings associated with the given LTI role
     * 
     * @param ltiRole the LTI role ID
     * @return the list of {@link KalturaLtiRole} objects
     */
    public List<KalturaLtiRole> getLtiRoleMappings(String ltiRole);

    /**
     * Add/update a new role mapping
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object to add
     * @return true, if added/updated successfully
     */
    public void save(KalturaLtiRole kalturaLtiRole);

    /**
     * Delete a role mapping
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object to delete
     * @return true, if marked inactive
     */
    public void delete(KalturaLtiRole kalturaLtiRole);

    /**
     * Delete a role mapping
     * 
     * @param id the string ID of the {@link KalturaLtiRole} object to delete
     * @return true, if marked inactive
     */
    public void delete(String id);

    /**
     * Delete a role mapping
     * 
     * @param id the long ID of the {@link KalturaLtiRole} object to delete
     * @return true, if marked inactive
     */
    public void delete(long id);

    /**
     * Commit the transaction
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object
     * @param delete is this a delete operation?
     */
    public void commit(KalturaLtiRole kalturaLtiRole, boolean delete);

}
