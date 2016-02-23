/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller to handle the ckeditor.jsp view
 *
 */
public class CKEditorController extends BaseController {
    final private Log log = LogFactory.getLog(getClass());

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ControllerRequestParameters parameters = processRequestParameters(request);
        log.debug(parameters.toString());

        // get the source code HTML form the LTI request
        Map<String,Object> model = new HashMap<String,Object>();
        String returnData[] = kalturaLTIService.launchCKEditorRequest("", parameters.getUserId(), parameters.getSiteId());
        model.put("returndata", returnData[0]);

        String view = serverConfigurationService.getBoolean("kaltura.ckeditor.debug", false) ? "ckeditordebug" : "ckeditor";

        return new ModelAndView(view, model);
    }

}
