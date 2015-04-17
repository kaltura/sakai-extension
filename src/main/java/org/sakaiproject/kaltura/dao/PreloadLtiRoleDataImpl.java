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
        long count = 0;
        try {
            count = kalturaLtiRoleDao.countAll(KalturaLtiRole.class);
        } catch (Exception e) {}

        if (log.isDebugEnabled()) {
            log.debug("Check for existing role mappings: " + count);
        }

        // preload default roles if none exist
        if (count == 0) {
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

            log.info("Preloaded " + kalturaLtiRoleDao.countAll(KalturaLtiRole.class) + " role mappings.");
        }
    }
}
