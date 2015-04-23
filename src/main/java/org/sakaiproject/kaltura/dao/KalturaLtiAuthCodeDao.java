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

import java.util.Date;
import java.util.List;

import org.sakaiproject.kaltura.models.db.KalturaLtiAuthCode;

/**
 * DAO Interface for authorization codes allowing access to RESTful APIs
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public interface KalturaLtiAuthCodeDao {

    /**
     * Gets a {@link KalturaLtiAuthCode} object that is associated with the given auth code ID
     * 
     * @param id the auth code ID
     * @return a {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode getAuthCode(long id);

    /**
     * Gets a {@link KalturaLtiAuthCode} object that is associated with the given auth code ID
     * 
     * @param id the auth code ID
     * @return a {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode getAuthCode(String id);

    /**
     * Gets a list of {@link KalturaLtiAuthCode} objects that are unused for the given user ID and auth code
     * 
     * @param userId the Sakai internal user ID
     * @param authCode the authorization code
     * @return a list of {@link KalturaLtiAuthCode} objects
     */
    public List<KalturaLtiAuthCode> getUnusedAuthCodes(String userId, String authCode);

    /**
     * Creates an auth code
     * 
     * @param kalturaLtiAuthCode the {@link KalturaLtiAuthCode} object
     * @return the {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode createAuthCode(KalturaLtiAuthCode kalturaLtiAuthCode);

    /**
     * Creates an auth code
     * 
     * @param userId the Sakai internal user ID
     * @return the {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode createAuthCode(String userId);

    /**
     * Creates an auth code
     * 
     * @param userId the Sakai internal user ID
     * @param authCode the auth code
     * @return the {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode);

    /**
     * Creates an auth code
     * 
     * @param userId the Sakai internal user ID
     * @param authCode the auth code
     * @param codeUsed should the code be marked as used?
     * @return the {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, boolean codeUsed);

    /**
     * Creates an auth code
     * 
     * @param userId the Sakai internal user ID
     * @param authCode the auth code
     * @param dateCreated the date of creation
     * @return the {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, Date dateCreated);

    /**
     * Creates an auth code
     * 
     * @param userId the Sakai internal user ID
     * @param authCode the auth code
     * @param codeUsed should the code be marked as used?
     * @param dateCreated the date of creation
     * @return the {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, boolean codeUsed, Date dateCreated);

    /**
     * Inactivates a code
     * 
     * @param id the id of the {@link KalturaLtiAuthCode} object
     * @return true, if marked "used"
     */
    public boolean inactivateAuthCode(long id);

    /**
     * Inactivates a code
     * 
     * @param authCode the auth code of the {@link KalturaLtiAuthCode} object
     * @return true, if marked "used"
     */
    public boolean inactivateAuthCode(String authCode);

    /**
     * Inactivates a code
     * 
     * @param userId the Sakai internal user id of the {@link KalturaLtiAuthCode} object
     * @param authCode the auth code of the {@link KalturaLtiAuthCode} object
     * @return true, if marked "used"
     */
    public boolean inactivateAuthCode(String userId, String authCode);

    /**
     * Inactivates a code
     * 
     * @param kalturaLtiAuthCode the {@link KalturaLtiAuthCode} object
     * @return true, if marked "used"
     */
    public boolean inactivateAuthCode(KalturaLtiAuthCode kalturaLtiAuthCode);

}
