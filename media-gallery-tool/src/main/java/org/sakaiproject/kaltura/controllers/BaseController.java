package org.sakaiproject.kaltura.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.kaltura.models.User;
import org.sakaiproject.kaltura.services.KalturaLTIService;
import org.sakaiproject.kaltura.services.SecurityService;
import org.sakaiproject.kaltura.services.UserService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

abstract class BaseController extends AbstractController {

    protected KalturaLTIService kalturaLTIService;
    protected SecurityService securityService;
    protected ToolManager toolManager;
    protected UserService userService;
    protected ServerConfigurationService serverConfigurationService;

    public void setKalturaLTIService(KalturaLTIService kalturaLTIService) {
        this.kalturaLTIService = kalturaLTIService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setToolManager(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    protected ControllerRequestParameters processRequestParameters(HttpServletRequest request) throws Exception {
        String siteId;

        // attempt to get current site ID
        Placement toolPlacement = toolManager.getCurrentPlacement();
        if (toolPlacement != null) {
            siteId = toolPlacement.getContext();
        } else {
            // no current site ID, use the passed-in value
            siteId = request.getParameter("siteid");
        }

        if (StringUtils.isBlank(siteId)) {
            throw new IllegalArgumentException("Site ID cannot be null.");
        }

        // check to see if current user has access to current site
        if (!securityService.isAllowedAccess(siteId)) {
            throw new IllegalAccessException("Current user is not allowed to access site with ID: " + siteId);
        }

        // get the current user
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalAccessException("The current user is not defined");
        }

        String mediaItemUrl = request.getParameter("mediaitemurl");

        String entryId = request.getParameter("entryid");

        return new ControllerRequestParameters(currentUser.getId(), siteId, mediaItemUrl, entryId);
    }

    /**
     * Object to hold the various launch parameters
     */
    protected class ControllerRequestParameters {
        private String userId;
        private String siteId;
        private String mediaItemUrl;
        private String entryId;

        public ControllerRequestParameters() {}

        public ControllerRequestParameters(String userId, String siteId) {
            this.userId = userId;
            this.siteId = siteId;
        }

        public ControllerRequestParameters(String userId, String siteId, String mediaItemUrl) {
            this.userId = userId;
            this.siteId = siteId;
            this.mediaItemUrl = mediaItemUrl;
        }

        public ControllerRequestParameters(String userId, String siteId, String mediaItemUrl, String entryId) {
            this.userId = userId;
            this.siteId = siteId;
            this.mediaItemUrl = mediaItemUrl;
            this.entryId = entryId;
        }

        public String getUserId() {
            return userId;
        }
        public void setUserId(String userId) {
            this.userId = StringUtils.trimToEmpty(userId);
        }
        public String getSiteId() {
            return siteId;
        }
        public void setSiteId(String siteId) {
            this.siteId = StringUtils.trimToEmpty(siteId);
        }
        public String getMediaItemUrl() {
            return mediaItemUrl;
        }
        public void setMediaItemUrl(String mediaItemUrl) {
            this.mediaItemUrl = StringUtils.trimToEmpty(mediaItemUrl);
        }
        public String getEntryId() {
            return entryId;
        }
        public void setEntryId(String entryId) {
            this.entryId = StringUtils.trimToEmpty(entryId);
        }

        @Override
        public String toString() {
            return "request parameters:: mediaItemUrl:entryId:userId:siteId [" + this.mediaItemUrl + ":" + this.entryId + ":" + this.userId + ":" + this.siteId + "]";
        }
    }

    /**
     * @see AbstractController#handleRequestInternal(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
