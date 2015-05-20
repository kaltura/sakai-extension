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
package org.sakaiproject.kaltura.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.dao.KalturaLtiAuthCodeDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiAuthCode;

/**
 * A helper class for getting, updating, and calculating authorization code objects
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class AuthCodeService {

    private KalturaLtiAuthCodeDao kalturaLtiAuthCodeDao;
    public void setKalturaLtiAuthCodeDao(KalturaLtiAuthCodeDao kalturaLtiAuthCodeDao) {
        this.kalturaLtiAuthCodeDao = kalturaLtiAuthCodeDao;
    }

    /**
     * {@link KalturaLtiAuthCodeDao#getAuthCode(long)}
     */
    public KalturaLtiAuthCode getAuthCode(Long id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("AuthCodeService :: auth code ID cannot be null.");
        }

        return kalturaLtiAuthCodeDao.getAuthCode(id);
    }

    /**
     * {@link KalturaLtiAuthCodeDao#getAuthCode(String)}
     */
    public KalturaLtiAuthCode getAuthCode(String id) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("AuthCodeService :: auth code ID cannot be null.");
        }

        return kalturaLtiAuthCodeDao.getAuthCode(id);
    }

    /**
     * {@link KalturaLtiAuthCodeDao#createAuthCode(String)}
     */
    public KalturaLtiAuthCode createAuthCode(String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("AuthCodeService :: user ID cannot be null.");
        }

        return kalturaLtiAuthCodeDao.createAuthCode(userId);
    }

    /**
     * {@link KalturaLtiAuthCodeDao#createAuthCode(String, String)}
     */
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("AuthCodeService :: user ID cannot be null.");
        }
        if (StringUtils.isBlank(authCode)) {
            throw new IllegalArgumentException("AuthCodeService :: auth code cannot be null.");
        }

        return kalturaLtiAuthCodeDao.createAuthCode(userId, authCode);
    }

    /**
     * {@link KalturaLtiAuthCodeDao#createAuthCode(String, String, Date)}
     */

    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, Date dateCreated) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("AuthCodeService :: user ID cannot be null.");
        }
        if (StringUtils.isBlank(authCode)) {
            throw new IllegalArgumentException("AuthCodeService :: auth code cannot be null.");
        }
        if (dateCreated == null) {
            dateCreated = new Date();
        }

        return kalturaLtiAuthCodeDao.createAuthCode(userId, authCode, dateCreated);
    }

    /**
     * {@link KalturaLtiAuthCodeDao#createAuthCode(KalturaLtiAuthCode)}
     */
    public KalturaLtiAuthCode createAuthCode(KalturaLtiAuthCode kalturaLtiAuthCode) throws Exception {
        if (kalturaLtiAuthCode == null) {
            throw new IllegalArgumentException("AuthCodeService :: kalturaLtiAuthCode cannot be null.");
        }

        return kalturaLtiAuthCodeDao.createAuthCode(kalturaLtiAuthCode);
    }

    /**
     * Checks to see if the authorization code and user ID mapping is valid (i.e. not expired)
     * 
     * @param authorizationCode the authorization code
     * @param userId the internal Sakai user ID
     * @return true, if valid
     * @throws Exception
     */
    public boolean isValid(String authorizationCode, String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("AuthCodeService :: user ID cannot be null.");
        }
        if (StringUtils.isBlank(authorizationCode)) {
            throw new IllegalArgumentException("AuthCodeService :: authorization code cannot be null.");
        }

        KalturaLtiAuthCode kalturaLtiAuthCode = kalturaLtiAuthCodeDao.getAuthCode(authorizationCode, userId);

        if (kalturaLtiAuthCode == null) {
            // no auth code : user ID mapping is found, code is invalid
            return false;
        }

        // if current date is after the expiration date, code is invalid
        return (new Date()).after(kalturaLtiAuthCode.getDateExpires());
    }

}
