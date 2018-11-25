package org.sakaiproject.kaltura.services;

import java.util.Properties;

import org.sakaiproject.user.api.User;

public interface KalturaLTIService {
    String[] launchLTIRequest(String module);

    String[] launchLTIRequest(String module, User user, String placementId, String siteId);

    String[] launchCKEditorRequest(String module, String userId, String siteId);

    String[] launchCKEditorRequest(String module, User user, String placementId, String siteId);

    String[] launchLTIDisplayRequest(String module, String userId, String siteId);

    String[] launchLTIDisplayStaticRequest(String entryId, String userId, String siteId);

    String[] launchLTIDisplayRequest(String launchUrl, User user, String siteId, String placementId);

    Properties prepareSiteCopyRequest(String module, String fromSiteId, String targetSiteId);
}
