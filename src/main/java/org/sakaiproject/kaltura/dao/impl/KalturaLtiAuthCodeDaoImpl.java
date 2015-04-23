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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.kaltura.dao.KalturaLtiAuthCodeDao;
import org.sakaiproject.kaltura.models.db.KalturaLtiAuthCode;
import org.sakaiproject.kaltura.utils.common.AuthCodeUtil;

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
    public List<KalturaLtiAuthCode> getUnusedAuthCodes(String userId, String authCode) {
        String[] properties = new String[] {
            "userID",
            "authCode",
            "used"
        };
        Object[] values = new Object[] {
            userId,
            authCode,
            false
        };
        Search search = new Search(properties, values);

        List<KalturaLtiAuthCode> unusedAuthCodes = findBySearch(KalturaLtiAuthCode.class, search);

        return unusedAuthCodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId) {
        String authorizationCode = AuthCodeUtil.createNewAuthorizationCode();

        return createAuthCode(userId, authorizationCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode) {
        return createAuthCode(userId, authCode, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, boolean codeUsed) {
        return createAuthCode(userId, authCode, codeUsed, new Date());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, Date dateCreated) {
        return createAuthCode(userId, authCode, false, dateCreated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(String userId, String authCode, boolean codeUsed, Date dateCreated) {
        KalturaLtiAuthCode kalturaLtiAuthCode = new KalturaLtiAuthCode(userId, authCode, codeUsed, dateCreated, AuthCodeUtil.calculateExpirationDate(dateCreated));

        return createAuthCode(kalturaLtiAuthCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalturaLtiAuthCode createAuthCode(KalturaLtiAuthCode kalturaLtiAuthCode) {
        save(kalturaLtiAuthCode);

        return kalturaLtiAuthCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inactivateAuthCode(long id) {
        Search search = new Search("id", id);

        KalturaLtiAuthCode kalturaLtiAuthCode = findOneBySearch(KalturaLtiAuthCode.class, search);

        return inactivateAuthCode(kalturaLtiAuthCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inactivateAuthCode(String authCode) {
        Search search = new Search("authCode", authCode);

        KalturaLtiAuthCode kalturaLtiAuthCode = findOneBySearch(KalturaLtiAuthCode.class, search);

        return inactivateAuthCode(kalturaLtiAuthCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inactivateAuthCode(String userId, String authCode) {
        List<KalturaLtiAuthCode> unusedAuthCodes = getUnusedAuthCodes(userId, authCode);

        for (KalturaLtiAuthCode kalturaLtiAuthCode : unusedAuthCodes) {
            inactivateAuthCode(kalturaLtiAuthCode);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inactivateAuthCode(KalturaLtiAuthCode kalturaLtiAuthCode) {
        if (kalturaLtiAuthCode == null) {
            throw new IllegalArgumentException("Authorization code object cannot be null.");
        }

        kalturaLtiAuthCode.setInactivated(true);

        try {
            save(kalturaLtiAuthCode);
        } catch (Exception e) {
            log.error("Error inactivating authorization code. Error: " + e, e);

            return false;
        }

        return true;
    }

}
