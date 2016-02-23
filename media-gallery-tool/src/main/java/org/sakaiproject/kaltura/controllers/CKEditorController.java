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
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.kaltura.models.User;
import org.sakaiproject.kaltura.services.KalturaLTIService;
import org.sakaiproject.kaltura.services.SecurityService;
import org.sakaiproject.kaltura.services.UserService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller to handle the ckeditor.jsp view
 * 
 * @author Yegeneswari Nagappan (ynagappan @ unicon.net)
 *
 */
public class CKEditorController extends AbstractController {
    final protected Log log = LogFactory.getLog(getClass());

    private KalturaLTIService kalturaLTIService;
    public void setKalturaLTIService(KalturaLTIService kalturaLTIService) {
        this.kalturaLTIService = kalturaLTIService;
    }

    private SecurityService securityService;
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    private ToolManager toolManager;
    public void setToolManager(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    private UserService userService;
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String currentSiteId;

        // attempt to get current site ID
        Placement toolPlacement = toolManager.getCurrentPlacement();
        if (toolPlacement != null) {
            currentSiteId = toolPlacement.getContext();
        } else {
            currentSiteId = request.getParameter("siteid");
        }

        if (StringUtils.isBlank(currentSiteId)) {
            throw new IllegalArgumentException("Site ID cannot be null.");
        }

        // check to see if current user has access to current site
        if (!securityService.isAllowedAccess(currentSiteId)) {
            throw new IllegalAccessException("Current user is not allowed to access site with ID: " + currentSiteId);
        }

        // get the current user
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalAccessException("The current user is not defined");
        }

        // get the source code HTML form the LTI request
        Map<String,Object> model = new HashMap<String,Object>();
        String returnData[] = kalturaLTIService.launchCKEditorRequest("", currentUser.getId(), currentSiteId);
        model.put("returndata", returnData[0]);

        String view = serverConfigurationService.getBoolean("kaltura.ckeditor.debug", false) ? "ckeditordebug" : "ckeditor";

        return new ModelAndView(view, model);
    }

}
