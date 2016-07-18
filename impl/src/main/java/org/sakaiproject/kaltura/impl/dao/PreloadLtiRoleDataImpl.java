/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.api.dao.KalturaLtiRoleDao;
import org.sakaiproject.kaltura.models.dao.KalturaLtiRole;

/**
 * Service to pre-load role mappings on first boot, if configured
 * 
 * @author Robert Long (rlong @ unicon.net)
 *
 */
public class PreloadLtiRoleDataImpl {

    private static final Logger log = LoggerFactory.getLogger(PreloadLtiRoleDataImpl.class);

    private KalturaLtiRoleDao kalturaLtiRoleDao;
    public void setKalturaLtiRoleDao(KalturaLtiRoleDao kalturaLtiRoleDao) {
        this.kalturaLtiRoleDao = kalturaLtiRoleDao;
    }

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    public void init() {
        preloadDefaultRoles();
    }

    /**
     * Preload the default role mappings into the database
     */
    public void preloadDefaultRoles() {
        boolean doPreload = serverConfigurationService.getBoolean("kaltura.lti.roles.preload", true);

        if (doPreload) {
            try {
                List<KalturaLtiRole> existingRoles = kalturaLtiRoleDao.getAllRoleMappings();
    
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
                            kalturaLtiRoleDao.save(role);
    
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
        } else {
            log.info("Kaltura :: Pre-loading of default roles set to false. Nothing to do.");
        }
    }

}
