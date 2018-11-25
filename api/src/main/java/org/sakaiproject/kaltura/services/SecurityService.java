package org.sakaiproject.kaltura.services;

public interface SecurityService {
    boolean isAdmin();

    void securityCheck();

    void securityCheck(String authorizationCode, String userId);

    boolean isAllowedAccess(String siteId);

    boolean isAllowedAccess(String userRef, String siteRef);
}
