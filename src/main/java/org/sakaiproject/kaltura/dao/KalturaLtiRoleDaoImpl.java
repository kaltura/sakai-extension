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

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;

public class KalturaLtiRoleDaoImpl extends HibernateGeneralGenericDao implements KalturaLtiRoleDao {

    public void init() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KalturaLtiRole> getAllRoleMappings() {
        List<KalturaLtiRole> allRoleMappings = new ArrayList<KalturaLtiRole>();

        allRoleMappings = findAll(KalturaLtiRole.class);

        return allRoleMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KalturaLtiRole> getActiveRoleMappings() {
        List<KalturaLtiRole> activeRoleMappings = new ArrayList<KalturaLtiRole>();

        return activeRoleMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KalturaLtiRole> getInactiveRoleMappings() {
        List<KalturaLtiRole> inactiveRoleMappings = new ArrayList<KalturaLtiRole>();

        return inactiveRoleMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiRole getSakaiRoleMapping(String sakaiRole) {
        KalturaLtiRole kalturaLtiRole = new KalturaLtiRole();
        kalturaLtiRole.setSakaiRole(sakaiRole);

        return kalturaLtiRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiRole getLtiRoleMapping(String ltiRole) {
        KalturaLtiRole kalturaLtiRole = new KalturaLtiRole();
        kalturaLtiRole.setLtiRole(ltiRole);

        return kalturaLtiRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiRole addRoleMapping(KalturaLtiRole kalturaLtiRole) {
        return kalturaLtiRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiRole updateRoleMapping(KalturaLtiRole kalturaLtiRole) {
        return kalturaLtiRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteRoleMapping(KalturaLtiRole kalturaLtiRoleDB) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() {
        this.getSession().flush();
    }

}
