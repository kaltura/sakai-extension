/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.kaltura.api.dao.KalturaLtiRoleDao;
import org.sakaiproject.kaltura.models.dao.KalturaLtiRole;

/**
 * Implementation of DAO Interface for Kaltura custom role mappings
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaLtiRoleDaoImpl extends HibernateGeneralGenericDao implements KalturaLtiRoleDao {

    private static final Logger log = LoggerFactory.getLogger(KalturaLtiRoleDaoImpl.class);

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
        if (!kalturaLtiRole.isValid()) {
            kalturaLtiRole = new KalturaLtiRole(kalturaLtiRole);
        }

        kalturaLtiRole.setDateModified(new Date());

        commit(kalturaLtiRole, false);
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
        KalturaLtiRole kalturaLtiRole = getRoleMapping(id);

        if (kalturaLtiRole != null) {
            commit(kalturaLtiRole, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit(KalturaLtiRole kalturaLtiRole, boolean delete) {
        getHibernateTemplate().flush();

        Session session = getSessionFactory().openSession();
        Transaction transaction = session.getTransaction();

        try {
            transaction = session.beginTransaction();

            if (delete) {
                session.delete(kalturaLtiRole);
            } else {
                session.saveOrUpdate(kalturaLtiRole);
            }

            transaction.commit();
        } catch ( Exception e) {
            if (delete) {
                log.error("Kaltura :: deleteRoleMapping : An error occurred deleting the role mapping: " + kalturaLtiRole.toString() + ", error: " + e, e);
            } else {
                log.error("Kaltura :: addRoleMapping : An error occurred persisting the role mapping: " + kalturaLtiRole.toString() + ", error: " + e, e);
            }

            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

}
