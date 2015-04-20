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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;

public class PreloadLtiRoleDataImpl {

    private static Log log = LogFactory.getLog(PreloadLtiRoleDataImpl.class);

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
                    for (String roleMapping : defaultRoleMapping) {
                        String[] roleMap = roleMapping.split(":");
                        String sakaiRole = roleMap[0];
                        String ltiRole = roleMap[1];
    
                        if (StringUtils.isNotBlank(sakaiRole) && StringUtils.isNotBlank(ltiRole)) {
                            KalturaLtiRole role = new KalturaLtiRole(sakaiRole, ltiRole, true);
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
