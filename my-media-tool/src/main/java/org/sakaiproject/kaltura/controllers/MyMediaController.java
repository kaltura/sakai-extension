/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.services.KalturaLTIService;

/**
 * Controller to handle the main.jsp view
 * 
 * @author Yegeneswari Nagappan (ynagappan @ unicon.net)
 *
 */
public class MyMediaController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(MyMediaController.class);

    private KalturaLTIService kalturaLTIService;
    public void setKalturaLTIService(KalturaLTIService kalturaLTIService) {
        this.kalturaLTIService = kalturaLTIService;
    }

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> model = new HashMap<String,Object>();
        String retval[] = kalturaLTIService.launchLTIRequest(Constants.MY_MEDIA);
        model.put("returndata", retval[0]);
        String isDebug = "kaltura." + Constants.MY_MEDIA + ".debug";
        model.put("isDebug", serverConfigurationService.getString(isDebug, "off"));
        return new ModelAndView("main", model);
    }

}
