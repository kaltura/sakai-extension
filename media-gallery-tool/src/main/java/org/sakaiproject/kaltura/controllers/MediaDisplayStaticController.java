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
import org.sakaiproject.kaltura.models.User;
import org.sakaiproject.kaltura.services.KalturaLTIService;
import org.sakaiproject.kaltura.services.SecurityService;
import org.sakaiproject.kaltura.services.UserService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class MediaDisplayStaticController extends AbstractController {
    final protected Log log = LogFactory.getLog(getClass());

    private KalturaLTIService kalturaLTIService;
    public void setKalturaLTIService(KalturaLTIService kalturaLTIService) {
        this.kalturaLTIService = kalturaLTIService;
    }

    private SecurityService securityService;
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
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
            // no current site ID, use the passed-in value
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

        Map<String,Object> model = new HashMap<String,Object>();
        String entryId = request.getParameter("entryid");

        log.debug("request params: entryid:userid:siteid [" + entryId + ":" + currentUser.getId() + ":" + currentSiteId + "]");
        if (StringUtils.isEmpty(entryId)) {
            model.put("returndata",  "NO MEDIA");
        } else {
            String returnData[] = kalturaLTIService.launchLTIDisplayStaticRequest(entryId, currentUser.getId(), currentSiteId);
            model.put("returndata", returnData[0]);
        }

        return new ModelAndView("mediadisplay", model);
    }

}
