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
package org.sakaiproject.kaltura.api.dao;

import java.util.Date;

import org.hibernate.Transaction;
import org.sakaiproject.genericdao.api.GeneralGenericDao;
import org.sakaiproject.kaltura.models.dao.KalturaLtiAuthCode;

/**
 * DAO Interface for authorization codes allowing access to RESTful APIs
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public interface KalturaLtiAuthCodeDao extends GeneralGenericDao {

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
     * Gets a {@link KalturaLtiAuthCode} object that is associated with the given auth code and Sakai internal user ID
     * 
     * @param authCode the auth code
     * @param userId the Sakai internal user ID
     * @return a {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode getAuthCode(String authCode, String userId);

    /**
     * Creates an auth code
     * 
     * @param kalturaLtiAuthCode the {@link KalturaLtiAuthCode} object
     * @return the {@link KalturaLtiAuthCode} object
     * @throws Exception 
     */
    public KalturaLtiAuthCode createAuthCode(KalturaLtiAuthCode kalturaLtiAuthCode) throws Exception;

    /**
     * Creates an auth code
     * 
     * @param userId the Sakai internal user ID
     * @param authCode the auth code
     * @param dateCreated the date of creation
     * @param dateExpires the date the code expires
     * @return the {@link KalturaLtiAuthCode} object
     * @throws Exception 
     */
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, Date dateCreated, Date dateExpires) throws Exception;

    /**
     * Add/update an authorization code
     * 
     * @param kalturaLtiAuthCode the {@link KalturaLtiAuthCode} object
     * 
     * @throws Exception 
     */
    public void save(KalturaLtiAuthCode kalturaLtiAuthCode) throws Exception;

    /**
     * Commit the transaction
     * 
     * @param kalturaLtiAuthCode the {@link KalturaLtiAuthCode} object
     */
    public void commit(KalturaLtiAuthCode kalturaLtiAuthCode);

}
