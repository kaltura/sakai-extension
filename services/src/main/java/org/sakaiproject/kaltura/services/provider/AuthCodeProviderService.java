/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.models.error.ErrorAuthCode;
import org.sakaiproject.kaltura.models.dao.KalturaLtiAuthCode;
import org.sakaiproject.kaltura.services.AuthCodeService;
import org.sakaiproject.kaltura.utils.JsonUtil;
import org.sakaiproject.kaltura.services.RestService;

/**
 * Service layer to support the kaltura/auth entities
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class AuthCodeProviderService {

    private static final Logger log = LoggerFactory.getLogger(AuthCodeProviderService.class);

    private AuthCodeService authCodeService;
    public void setAuthCodeService(AuthCodeService authCodeService) {
        this.authCodeService = authCodeService;
    }

    private RestService restService;
    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    public void init() {
    }

    /**
     * Get the {@link KalturaLtiAuthCode} object associated with this ID
     * 
     * @param id the id of the auth code
     */
    public ActionReturn getAuthCode(String id) {
        ErrorAuthCode errorAuthCode = new ErrorAuthCode();

        KalturaLtiAuthCode kalturaLtiAuthCode = null;

        try {
            kalturaLtiAuthCode = authCodeService.getAuthCode(id);
        } catch (Exception e) {
            errorAuthCode.updateErrorList(e.toString(), id, null);
            log.error(e.toString(), e);
        }

        if (kalturaLtiAuthCode.isExpired()) {
            errorAuthCode.updateErrorList("expired", kalturaLtiAuthCode.getAuthCode(), kalturaLtiAuthCode.getUserId());
        }

        return restService.processActionReturn(errorAuthCode, JsonUtil.parseToJson(kalturaLtiAuthCode));
    }

    /**
     * Create a new {@link KalturaLtiAuthCode} object
     * 
     * @param data the data to create the new {@link KalturaLtiAuthCode} object
     */
    public ActionReturn createAuthCode(String data) {
        ErrorAuthCode errorAuthCode = new ErrorAuthCode();

        List<Object> kalturaLtiAuthCodes = JsonUtil.parseFromJson(data, KalturaLtiAuthCode.class);

        for (Object k : kalturaLtiAuthCodes) {
            if (!(k instanceof KalturaLtiAuthCode)) {
                continue;
            }

            KalturaLtiAuthCode kalturaLtiAuthCode = (KalturaLtiAuthCode) k;

            try {
                authCodeService.createAuthCode(kalturaLtiAuthCode);
            } catch (Exception e) {
                errorAuthCode.updateErrorList(e.toString(), kalturaLtiAuthCode.getAuthCode(), kalturaLtiAuthCode.getUserId());
                log.error(e.toString(), e);
            }

        }

        return restService.processActionReturn(errorAuthCode);
    }

}
