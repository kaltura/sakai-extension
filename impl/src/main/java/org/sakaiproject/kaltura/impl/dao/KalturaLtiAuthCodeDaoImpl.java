/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.sakaiproject.kaltura.dao.KalturaLtiAuthCodeDao;
import org.sakaiproject.kaltura.models.dao.KalturaLtiAuthCode;
import org.springframework.transaction.annotation.Transactional;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of DAO Interface for authorization codes allowing access to RESTful APIs
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
@Slf4j
@Transactional
public class KalturaLtiAuthCodeDaoImpl implements KalturaLtiAuthCodeDao {

    @Setter
    private SessionFactory sessionFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getLastValidAuthCodeForUser(String userId) {
        CriteriaBuilder queryBuilder = sessionFactory.getCriteriaBuilder();

        CriteriaQuery<KalturaLtiAuthCode> query = queryBuilder.createQuery(KalturaLtiAuthCode.class);
        Root<KalturaLtiAuthCode> root = query.from(KalturaLtiAuthCode.class);

        Predicate byUserId = queryBuilder.equal(root.get("userId"), userId);
        Instant future = Instant.now().plus(2, ChronoUnit.MINUTES);
        Predicate isCurrent = queryBuilder.greaterThanOrEqualTo(root.get("dateExpires"), Date.from(future));

        query.select(root)
                .where(queryBuilder.and(byUserId, isCurrent))
                .orderBy(queryBuilder.desc(root.get("dateExpires")));

        Collection<KalturaLtiAuthCode> results = sessionFactory.getCurrentSession()
                .createQuery(query)
                .setMaxResults(1)
                .getResultList();

        if (results.isEmpty()) {
            return null;
        }
        return results.toArray(new KalturaLtiAuthCode[]{})[0];
    }

    public KalturaLtiAuthCode getAuthCode(long id) {
        return sessionFactory.getCurrentSession().get(KalturaLtiAuthCode.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getAuthCode(String authCode) {
        CriteriaBuilder queryBuilder = sessionFactory.getCriteriaBuilder();

        CriteriaQuery<KalturaLtiAuthCode> query = queryBuilder.createQuery(KalturaLtiAuthCode.class);
        Root<KalturaLtiAuthCode> root = query.from(KalturaLtiAuthCode.class);

        Predicate byAuthCode = queryBuilder.equal(root.get("authCode"), authCode);

        query.select(root).where(byAuthCode);

        Collection<KalturaLtiAuthCode> results = sessionFactory.getCurrentSession()
                .createQuery(query)
                .getResultList();

        if (results.isEmpty()) {
            return null;
        }
        return results.toArray(new KalturaLtiAuthCode[]{})[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getAuthCode(String authCode, String userId) {
        CriteriaBuilder queryBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<KalturaLtiAuthCode> query = queryBuilder.createQuery(KalturaLtiAuthCode.class);

        Root<KalturaLtiAuthCode> root = query.from(KalturaLtiAuthCode.class);

        Predicate byAuthCode = queryBuilder.equal(root.get("authCode"), authCode);
        Predicate byUserId = queryBuilder.equal(root.get("userId"), userId);

        query.select(root).where(queryBuilder.and(byAuthCode, byUserId));

        Collection<KalturaLtiAuthCode> results = sessionFactory.getCurrentSession()
                .createQuery(query)
                .getResultList();

        if (results.isEmpty()) {
            return null;
        }
        return results.toArray(new KalturaLtiAuthCode[]{})[0];
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

        return save(kalturaLtiAuthCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode save(KalturaLtiAuthCode kalturaLtiAuthCode) {
        if (!kalturaLtiAuthCode.isValid()) {
            kalturaLtiAuthCode = new KalturaLtiAuthCode(kalturaLtiAuthCode);
        }

        return (KalturaLtiAuthCode) sessionFactory.getCurrentSession().merge(kalturaLtiAuthCode);
    }
}
