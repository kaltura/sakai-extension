package org.sakaiproject.kaltura.services.provider;

import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.kaltura.services.KalturaLTIService;
import org.sakaiproject.kaltura.services.RestService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

public class CKEditorProviderService {

    private KalturaLTIService kalturaLTIService;
    public void setKalturaLTIService(KalturaLTIService kalturaLTIService) {
        this.kalturaLTIService = kalturaLTIService;
    }

    protected RestService restService;
    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    private SiteService siteService;
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    private UserDirectoryService userDirectoryService;
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    public void init() {
    }

    public ActionReturn launchLti(String siteId) throws IllegalAccessException {
        User currentUser = userDirectoryService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalAccessException("No current user id defined.");
        }
        String currentUserId = currentUser.getEid();

        if (!siteService.isCurrentUserMemberOfSite(siteId)) {
            throw new IllegalAccessException("Current user is not a member if site: " + siteId);
        }

        String[] source = kalturaLTIService.launchCKEditorRequest("", currentUserId, siteId);

        return restService.processActionReturn(null, source[0], Formats.HTML);
    }

}
