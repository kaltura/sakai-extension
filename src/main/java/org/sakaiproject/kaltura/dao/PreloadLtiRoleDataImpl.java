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
import org.sakaiproject.kaltura.models.db.KalturaLtiRole;

public class PreloadLtiRoleDataImpl {

    private static Log log = LogFactory.getLog(PreloadLtiRoleDataImpl.class);

    private KalturaLtiRoleDao kalturaLtiRoleDao;
    public void setKalturaLtiRoleDao(KalturaLtiRoleDao kalturaLtiRoleDao) {
        this.kalturaLtiRoleDao = kalturaLtiRoleDao;
    }

    public void init() {
        preloadItems();
    }

    /**
     * Preload the default role mappings into the database
     */
    public void preloadItems() {
        try {
            List<KalturaLtiRole> existingRoles = kalturaLtiRoleDao.getAllRoleMappings();

            // preload default roles if none exist
            if (existingRoles.isEmpty()) {
                KalturaLtiRole instructor = new KalturaLtiRole("Instructor", "Instructor", true);
                kalturaLtiRoleDao.save(instructor);
                KalturaLtiRole maintain = new KalturaLtiRole("maintain", "Instructor", true);
                kalturaLtiRoleDao.save(maintain);
                KalturaLtiRole student = new KalturaLtiRole("Student", "Learner", true);
                kalturaLtiRoleDao.save(student);
                KalturaLtiRole access = new KalturaLtiRole("access", "Learner", true);
                kalturaLtiRoleDao.save(access);
                KalturaLtiRole ta = new KalturaLtiRole("Teaching Assistant", "Instructor", true);
                kalturaLtiRoleDao.save(ta);

                log.info("Kaltura :: Preloaded default role mappings.");
            }
        } catch (Exception e) {
            log.error("Kaltura :: there was an error updating the default roles. " + e, e);
        }
    }
}
