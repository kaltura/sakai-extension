/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.models.error.BaseError;

public class RestService {

    /**
     * Convenience method to calculate the return data and the response HTTP code
     * 
     * @param errors the BaseError object containing any errors
     * @return the ActionReturn with the HTTP code and any errors
     */
    public ActionReturn processActionReturn(BaseError errors) {
        return processActionReturn(errors, null);
    }

    /**
     * Convenience method to calculate the return data and the response HTTP code
     * 
     * @param data the data to be returned
     * @return the ActionReturn with the HTTP code and any data
     */
    public ActionReturn processActionReturn(String data) {
        return processActionReturn(null, data);
    }

    /**
     * Calculates the return data and the response HTTP code
     * 
     * @param errors the BaseError object containing any errors
     * @param data the data to be returned
     * @return the ActionReturn with the HTTP code and any errors or data
     */
    public ActionReturn processActionReturn(BaseError errors, String data) {
        return processActionReturn(errors, data, Formats.JSON);
    }

    /**
     * Calculates the return data and the response HTTP code
     * 
     * @param errors the BaseError object containing any errors
     * @param data the data to be returned
     * @param format the return mime type
     * @return the ActionReturn with the HTTP code and any errors or data
     */
    public ActionReturn processActionReturn(BaseError errors, String data, String format) {
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

        ActionReturn actionReturn = new ActionReturn(Constants.REST_DEFAULT_ENCODING, format, rv);
        actionReturn.setResponseCode(responseCode);

        return actionReturn;
    }
}
