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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.kaltura.dao.api.KalturaLtiAuthCodeDao;
import org.sakaiproject.kaltura.dao.models.db.KalturaLtiAuthCode;
import org.sakaiproject.kaltura.utils.AuthCodeUtil;

/**
 * Implementation of DAO Interface for authorization codes allowing access to RESTful APIs
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaLtiAuthCodeDaoImpl extends HibernateGeneralGenericDao implements KalturaLtiAuthCodeDao {

    private final Log log = LogFactory.getLog(KalturaLtiAuthCodeDaoImpl.class);

    public void init(){
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getAuthCode(long id) {
        Search search = new Search("id", id);

        KalturaLtiAuthCode kalturaLtiAuthCode = findOneBySearch(KalturaLtiAuthCode.class, search);

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getAuthCode(String authCode) {
        Search search = new Search("authCode", authCode);

        KalturaLtiAuthCode kalturaLtiAuthCode = findOneBySearch(KalturaLtiAuthCode.class, search);

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode getAuthCode(String authCode, String userId) {
        String[] properties = new String[] {"sakaiRole", "ltiRole"};
        String[] values = new String[] {authCode, userId};
        Search search = new Search(properties, values);

        KalturaLtiAuthCode kalturaLtiAuthCode = findOneBySearch(KalturaLtiAuthCode.class, search);

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId) throws Exception {
        String authorizationCode = AuthCodeUtil.createNewAuthorizationCode();

        return createAuthCode(userId, authorizationCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode) throws Exception {
        return createAuthCode(userId, authCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, Date dateCreated) throws Exception {
        KalturaLtiAuthCode kalturaLtiAuthCode = new KalturaLtiAuthCode(userId, authCode, dateCreated, AuthCodeUtil.calculateExpirationDate(dateCreated));

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

        try {
            save(kalturaLtiAuthCode);
        } catch (Exception e) {
            String error = "Error inactivating authorization code. Error: " + e;
            log.error(error, e);
            throw new Exception(error, e);
        }

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean save(KalturaLtiAuthCode kalturaLtiAuthCode) throws Exception {
        try {
            if (!kalturaLtiAuthCode.isValid()) {
                kalturaLtiAuthCode = new KalturaLtiAuthCode(kalturaLtiAuthCode);
            }

            super.save(kalturaLtiAuthCode);
            commit();
        } catch (Exception e) {
            String error = "Kaltura :: save : An error occurred persisting the authorization code: " + kalturaLtiAuthCode.toString() + ", error: " + e;
            log.error(error, e);
            throw new Exception(error, e);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() {
        this.getSession().flush();
    }

}
