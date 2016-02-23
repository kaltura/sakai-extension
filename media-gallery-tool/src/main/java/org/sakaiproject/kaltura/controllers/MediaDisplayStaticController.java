/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

public class MediaDisplayStaticController extends BaseController {
    final private Log log = LogFactory.getLog(getClass());

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ControllerRequestParameters parameters = processRequestParameters(request);
        log.debug(parameters.toString());

        Map<String,Object> model = new HashMap<String,Object>();

        if (StringUtils.isEmpty(parameters.getEntryId())) {
            model.put("returndata",  "NO MEDIA");
        } else {
            String returnData[] = kalturaLTIService.launchLTIDisplayStaticRequest(parameters.getEntryId(), parameters.getUserId(), parameters.getSiteId());
            model.put("returndata", returnData[0]);
        }

        return new ModelAndView("mediadisplay", model);
    }

}
