/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.error;

import org.apache.commons.lang.StringUtils;

/**
 * The model for errors during auth code processing
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class ErrorAuthCode extends BaseError {

    /**
     * Update the auth code errors listing with an error
     * 
     * @param error the error message
     * @param authId the auth code id (may be null)
     * @param userId the Sakai internal user id (may be null)
     */
    @Override
    public void updateErrorList(String error, String authId, String userId) {
        String value = "Error: " + error;
        if (StringUtils.isNotBlank(authId)) {
            value += ", auth code id: " + authId;
        }
        if (StringUtils.isNotBlank(userId)) {
            value += ", user id: " + userId;
        }

        update(errors, value);
    }

}
