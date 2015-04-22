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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;

public class KalturaLtiRoleDaoImpl extends HibernateGeneralGenericDao implements KalturaLtiRoleDao {

    private final Log log = LogFactory.getLog(KalturaLtiRoleDaoImpl.class);

    public void init() {
    }

    public void destroy(){
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KalturaLtiRole> getAllRoleMappings() {
        List<KalturaLtiRole> allRoleMappings = findAll(KalturaLtiRole.class);

        return allRoleMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KalturaLtiRole> getActiveRoleMappings() {
        Search search = new Search("active", true);

        List<KalturaLtiRole> activeRoleMappings = findBySearch(KalturaLtiRole.class, search);

        return activeRoleMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KalturaLtiRole> getInactiveRoleMappings() {
        Search search = new Search("active", false);

        List<KalturaLtiRole> inactiveRoleMappings = findBySearch(KalturaLtiRole.class, search);

        return inactiveRoleMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiRole getRoleMapping(long roleMappingId) {
        Search search = new Search("id", roleMappingId);

        KalturaLtiRole kalturaLtiRole = findOneBySearch(KalturaLtiRole.class, search);

        return kalturaLtiRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiRole getRoleMapping(String roleMappingId) {
        long id = Long.parseLong(roleMappingId);

        return getRoleMapping(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiRole getSakaiRoleMapping(String sakaiRole) {
        Search search = new Search("sakaiRole", sakaiRole);

        KalturaLtiRole kalturaLtiRole = findOneBySearch(KalturaLtiRole.class, search);

        return kalturaLtiRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KalturaLtiRole> getLtiRoleMappings(String ltiRole) {
        Search search = new Search("ltiRole", ltiRole);

        List<KalturaLtiRole> ltiRoleMappings = findBySearch(KalturaLtiRole.class, search);

        return ltiRoleMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean persistRoleMapping(KalturaLtiRole kalturaLtiRole) {
        try {
            save(kalturaLtiRole);
        } catch ( Exception e) {
            log.error("Kaltura :: addRoleMapping : An error occurred persisting the role mapping: " + kalturaLtiRole.toString() + ", error: " + e, e);

            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteRoleMapping(KalturaLtiRole kalturaLtiRole) {
        kalturaLtiRole.setActive(false);

        return persistRoleMapping(kalturaLtiRole);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() {
        this.getSession().flush();
    }

}
