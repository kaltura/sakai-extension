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
package org.sakaiproject.kaltura.utils.common;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.models.errors.BaseError;

public class RestUtil {

    /**
     * Convenience method to calculate the return data and the response HTTP code
     * 
     * @param errors the BaseError object containing any errors
     * @return the ActionReturn with the HTTP code and any errors
     */
    public static ActionReturn processActionReturn(BaseError errors) {
        return processActionReturn(errors, null);
    }

    /**
     * Convenience method to calculate the return data and the response HTTP code
     * 
     * @param data the data to be returned
     * @return the ActionReturn with the HTTP code and any data
     */
    public static ActionReturn processActionReturn(String data) {
        return processActionReturn(null, data);
    }

    /**
     * Calculates the return data and the response HTTP code
     * 
     * @param errors the BaseError object containing any errors
     * @param data the data to be returned
     * @return the ActionReturn with the HTTP code and any errors or data
     */
    public static ActionReturn processActionReturn(BaseError errors, String data) {
        String rv = null;
        int responseCode = -1;

        if ((errors == null || errors.isEmpty()) && StringUtils.isBlank(data)) {
            // nothing to return
            responseCode = 204;
        } else if ((errors == null || errors.isEmpty()) && StringUtils.isNotBlank(data)) {
            // data to return
            rv = data;
            responseCode = 200;
        } else if (errors != null && !errors.isEmpty()) {
            // errors to return
            rv = errors.toString();
            responseCode = 400;
        }

        ActionReturn actionReturn = new ActionReturn(Constants.REST_DEFAULT_ENCODING, Formats.JSON, rv);
        actionReturn.setResponseCode(responseCode);

        return actionReturn;
    }

}
