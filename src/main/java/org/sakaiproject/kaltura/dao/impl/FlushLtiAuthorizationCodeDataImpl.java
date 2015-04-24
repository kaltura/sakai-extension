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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.kaltura.dao.KalturaLtiAuthCodeDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiAuthCode;

/**
 * Service to flush inactivated authorization codes from the database, if configured
 * 
 * @author Robert Long (rlong @ unicon.net)
 *
 */
public class FlushLtiAuthorizationCodeDataImpl {

    private static Log log = LogFactory.getLog(FlushLtiAuthorizationCodeDataImpl.class);

    private KalturaLtiAuthCodeDao kalturaLtiAuthCodeDao;
    public void setKalturaLtiAuthCodeDao(KalturaLtiAuthCodeDao kalturaLtiAuthCodeDao) {
        this.kalturaLtiAuthCodeDao = kalturaLtiAuthCodeDao;
    }

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    public void init() {
        flushAuthorizationCodes();
    }

    /**
     * Remove all inactivated authorization codes from the database
     */
    public void flushAuthorizationCodes() {
        boolean doFlush = serverConfigurationService.getBoolean("kaltura.lti.flush.inactivated.authorization.codes", false);

        if (doFlush) {
            try {
                Search search = new Search("inactivated", true);

                List<KalturaLtiAuthCode> inactivatedAuthCodes = kalturaLtiAuthCodeDao.findBySearch(KalturaLtiAuthCode.class, search);

                for (KalturaLtiAuthCode inactivatedAuthCode : inactivatedAuthCodes) {
                    kalturaLtiAuthCodeDao.delete(inactivatedAuthCode);
                }

                log.info("Kaltura :: Flushed inactivated authorization codes.");
            } catch (Exception e) {
                log.error("Kaltura :: there was an error flushing the authorization codes. " + e, e);
            }
        } else {
            log.info("Kaltura :: Flushing of authorization codes set to false. Nothing to do.");
        }
    }
}
