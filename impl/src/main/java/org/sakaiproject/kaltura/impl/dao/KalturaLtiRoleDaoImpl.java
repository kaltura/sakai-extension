/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.dao.KalturaLtiRoleDao;
import org.sakaiproject.kaltura.models.dao.KalturaLtiRole;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of DAO Interface for Kaltura custom role mappings
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
@Slf4j
@Transactional
public class KalturaLtiRoleDaoImpl implements KalturaLtiRoleDao {

    @Setter
    private SessionFactory sessionFactory;

    @Setter
    private ServerConfigurationService serverConfigurationService;

    @Setter
    TransactionTemplate transactionTemplate;

    public void init() {
        if (serverConfigurationService.getBoolean("kaltura.lti.roles.preload", true)) {
            transactionTemplate.executeWithoutResult(action -> preloadDefaultLtiRoles());
        } else {
            log.info("Kaltura :: Pre-loading of default roles set to false. Nothing to do.");
        }
    }

    @Override
    public List<KalturaLtiRole> getAllRoleMappings() {
        CriteriaBuilder queryBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<KalturaLtiRole> query = queryBuilder.createQuery(KalturaLtiRole.class);

        Root<KalturaLtiRole> root = query.from(KalturaLtiRole.class);

        query.select(root);

        return sessionFactory.getCurrentSession()
                .createQuery(query)
                .getResultList();
    }

    @Override
    public KalturaLtiRole getRoleMapping(long roleMappingId) {
        return sessionFactory.getCurrentSession().get(KalturaLtiRole.class, roleMappingId);
    }

    @Override
    public KalturaLtiRole getRoleMapping(String roleMappingId) {
        long id = Long.parseLong(roleMappingId);

        return getRoleMapping(id);
    }

    @Override
    public List<KalturaLtiRole> getSakaiRoleMappings(String sakaiRole) {
        CriteriaBuilder queryBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<KalturaLtiRole> query = queryBuilder.createQuery(KalturaLtiRole.class);

        Root<KalturaLtiRole> root = query.from(KalturaLtiRole.class);

        Predicate bySakaiRole = queryBuilder.equal(root.get("sakaiRole"), sakaiRole);
        query.select(root).where(bySakaiRole);

        return sessionFactory.getCurrentSession()
                .createQuery(query)
                .getResultList();
    }

    @Override
    public List<KalturaLtiRole> getLtiRoleMappings(String ltiRole) {
        CriteriaBuilder queryBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<KalturaLtiRole> query = queryBuilder.createQuery(KalturaLtiRole.class);

        Root<KalturaLtiRole> root = query.from(KalturaLtiRole.class);

        Predicate byLtiRole = queryBuilder.equal(root.get("ltiRole"), ltiRole);
        query.select(root).where(byLtiRole);

        return sessionFactory.getCurrentSession()
                .createQuery(query)
                .getResultList();
    }

    @Override
    public KalturaLtiRole getRoleMapping(String sakaiRole, String ltiRole) {
        CriteriaBuilder queryBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<KalturaLtiRole> query = queryBuilder.createQuery(KalturaLtiRole.class);

        Root<KalturaLtiRole> root = query.from(KalturaLtiRole.class);

        Predicate bySakaiRole = queryBuilder.equal(root.get("sakaiRole"), sakaiRole);
        Predicate byLtiRole = queryBuilder.equal(root.get("ltiRole"), ltiRole);
        query.select(root).where(queryBuilder.and(bySakaiRole, byLtiRole));

        Collection<KalturaLtiRole> results = sessionFactory.getCurrentSession()
                .createQuery(query)
                .getResultList();

        if (results.isEmpty()) {
            return null;
        }
        return results.toArray(new KalturaLtiRole[]{})[0];
    }

    @Override
    public KalturaLtiRole save(KalturaLtiRole kalturaLtiRole) {
        if (!kalturaLtiRole.isValid()) {
            kalturaLtiRole = new KalturaLtiRole(kalturaLtiRole);
        }

        kalturaLtiRole.setDateModified(new Date());

        return (KalturaLtiRole) sessionFactory.getCurrentSession().merge(kalturaLtiRole);
    }

    @Override
    public void delete(KalturaLtiRole kalturaLtiRole) {
        if (kalturaLtiRole.getId() != null) {
            Session session = sessionFactory.getCurrentSession();
            session.delete(session.merge(kalturaLtiRole));
        }
    }

    @Override
    public void delete(String id) {
        long longId = Long.parseLong(id);

        delete(longId);
    }

    @Override
    public void delete(long id) {
        KalturaLtiRole kalturaLtiRole = getRoleMapping(id);

        if (kalturaLtiRole != null) {
            delete(kalturaLtiRole);
        }
    }

    private void preloadDefaultLtiRoles() {
        try {
            List<KalturaLtiRole> existingRoles = getAllRoleMappings();

            // preload default roles if none exist
            if (existingRoles.isEmpty()) {
                String[] defaultRoleMapping = serverConfigurationService.getStrings("kaltura.lti.roles");
                if (defaultRoleMapping == null) {
                    // none configured in sakai.properties, use hard-coded defaults
                    defaultRoleMapping = Constants.DEFAULT_ROLE_MAPPING;
                }

                for (String roleMapping : defaultRoleMapping) {
                    String[] roleMap = roleMapping.split(":");
                    String sakaiRole = roleMap[0];
                    String ltiRole = roleMap[1];

                    if (StringUtils.isNotBlank(sakaiRole) && StringUtils.isNotBlank(ltiRole)) {
                        KalturaLtiRole role = new KalturaLtiRole(sakaiRole, ltiRole);
                        save(role);

                        log.info("Kaltura :: Created default role mapping of Sakai role: " + sakaiRole + " --> " + "LTI role: " + ltiRole);
                    }
                }

                log.info("Kaltura :: Preloaded default role mappings.");
            } else {
                log.info("Kaltura :: Role mappings exist. Skipping pre-loading.");
            }
        } catch (Exception e) {
            log.error("Kaltura :: there was an error updating the default roles. " + e, e);
        }
    }
}
