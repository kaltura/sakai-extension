/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.error;

import org.apache.commons.lang.StringUtils;

/**
 * The model for errors during user processing
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class ErrorUser extends BaseError {

    /**
     * Update the user processing errors listing with an error
     * 
     * @param error the error message
     * @param action the action being performed (may be null)
     * @param userId the user id
     */
    @Override
    public void updateErrorList(String error, String action, String userId) {
        String value = "Error: " + error;
        if (StringUtils.isNotBlank(action)) {
            value += ", action: " + action;
        }
        if (StringUtils.isNotBlank(userId)) {
            value += ", userId: " + userId;
        }

        update(errors, value);
    }

}
