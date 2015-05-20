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
package org.sakaiproject.kaltura.dao.models.errors;

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
