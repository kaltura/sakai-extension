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
package org.sakaiproject.kaltura.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.kaltura.dao.KalturaLtiRoleDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;

/**
 * Implementation of DAO Interface for Kaltura custom role mappings
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
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
    public List<KalturaLtiRole> getSakaiRoleMappings(String sakaiRole) {
        Search search = new Search("sakaiRole", sakaiRole);

        List<KalturaLtiRole> kalturaLtiRoles = findBySearch(KalturaLtiRole.class, search);

        return kalturaLtiRoles;
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
    public KalturaLtiRole getRoleMapping(String sakaiRole, String ltiRole) {
        String[] properties = new String[] {"sakaiRole", "ltiRole"};
        String[] values = new String[] {sakaiRole, ltiRole};
        Search search = new Search(properties, values);

        KalturaLtiRole kalturaLtiRole = findOneBySearch(KalturaLtiRole.class, search);

        return kalturaLtiRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(KalturaLtiRole kalturaLtiRole) {
        try {
            if (!kalturaLtiRole.isValid()) {
                kalturaLtiRole = new KalturaLtiRole(kalturaLtiRole);
            }

            kalturaLtiRole.setDateModified(new Date());

            super.save(kalturaLtiRole);
        } catch ( Exception e) {
            log.error("Kaltura :: addRoleMapping : An error occurred persisting the role mapping: " + kalturaLtiRole.toString() + ", error: " + e, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(KalturaLtiRole kalturaLtiRole) {
        delete(kalturaLtiRole.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        long longId = Long.parseLong(id);

        delete(longId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(long id) {
        super.delete(KalturaLtiRole.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() {
        this.getSession().flush();
    }

}
