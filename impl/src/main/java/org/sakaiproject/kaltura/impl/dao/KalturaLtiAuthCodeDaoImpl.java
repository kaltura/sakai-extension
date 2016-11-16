/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.kaltura.api.dao.KalturaLtiAuthCodeDao;
import org.sakaiproject.kaltura.models.dao.KalturaLtiAuthCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Implementation of DAO Interface for authorization codes allowing access to RESTful APIs
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaLtiAuthCodeDaoImpl extends HibernateGeneralGenericDao implements KalturaLtiAuthCodeDao {

    private static final Logger log = LoggerFactory.getLogger(KalturaLtiAuthCodeDaoImpl.class);

    public void init(){
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getAuthCode(long id) {
        Search search = new Search("id", id);

        KalturaLtiAuthCode kalturaLtiAuthCode = findOneBySearch(KalturaLtiAuthCode.class, search);

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getAuthCode(String authCode) {
        Search search = new Search("authCode", authCode);

        KalturaLtiAuthCode kalturaLtiAuthCode = findOneBySearch(KalturaLtiAuthCode.class, search);

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getAuthCode(String authCode, String userId) {
        String[] properties = new String[] {"authCode", "userId"};
        String[] values = new String[] {authCode, userId};
        Search search = new Search(properties, values);

        KalturaLtiAuthCode kalturaLtiAuthCode = findOneBySearch(KalturaLtiAuthCode.class, search);

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, Date dateCreated, Date dateExpires) throws Exception {
        KalturaLtiAuthCode kalturaLtiAuthCode = new KalturaLtiAuthCode(userId, authCode, dateCreated, dateExpires);

        return createAuthCode(kalturaLtiAuthCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(KalturaLtiAuthCode kalturaLtiAuthCode) throws Exception {
        if (!kalturaLtiAuthCode.isValid()) {
            kalturaLtiAuthCode = new KalturaLtiAuthCode(kalturaLtiAuthCode);
        }

        try {
            save(kalturaLtiAuthCode);
        } catch (Exception e) {
            String error = "Error inactivating authorization code. Error: " + e;
            log.error(error, e);
            throw new Exception(error, e);
        }

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(KalturaLtiAuthCode kalturaLtiAuthCode) throws Exception {
        if (!kalturaLtiAuthCode.isValid()) {
            kalturaLtiAuthCode = new KalturaLtiAuthCode(kalturaLtiAuthCode);
        }

        commit(kalturaLtiAuthCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit(KalturaLtiAuthCode kalturaLtiAuthCode) {
        getHibernateTemplate().flush();

        Session session = getSessionFactory().openSession();
        Transaction transaction = session.getTransaction();

        try {
            transaction = session.beginTransaction();

            session.saveOrUpdate(kalturaLtiAuthCode);

            transaction.commit();
        } catch ( Exception e) {
            log.error("Kaltura :: addAuthCode : An error occurred persisting the authorization code: " + kalturaLtiAuthCode.toString() + ", error: " + e, e);

            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

}
