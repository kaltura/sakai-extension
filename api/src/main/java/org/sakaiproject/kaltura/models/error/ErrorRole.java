/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.error;

import org.apache.commons.lang.StringUtils;

/**
 * The model for errors during role processing
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class ErrorRole extends BaseError {

    /**
     * Update the role errors listing with an error
     * 
     * @param error the error message
     * @param site the site id (may be null)
     * @param eid the user's eid (may be null)
     */
    @Override
    public void updateErrorList(String error, String action, String roleId) {
        String value = "Error: " + error;
        if (StringUtils.isNotBlank(action)) {
            value += ", action: " + action;
        }
        if (StringUtils.isNotBlank(roleId)) {
            value += ", object: " + roleId;
        }

        update(errors, value);
    }

}
