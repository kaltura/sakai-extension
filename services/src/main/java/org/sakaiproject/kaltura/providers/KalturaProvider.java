/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.providers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.kaltura.services.provider.AuthCodeProviderService;
import org.sakaiproject.kaltura.services.provider.CKEditorProviderService;
import org.sakaiproject.kaltura.services.provider.GitProviderService;
import org.sakaiproject.kaltura.services.provider.RoleProviderService;
import org.sakaiproject.kaltura.services.provider.UserProviderService;
import org.sakaiproject.kaltura.services.SecurityService;
import org.springframework.web.servlet.ModelAndView;

/**
 * The provider for the REST API
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaProvider extends AbstractEntityProvider implements RESTful {

    private final Log log = LogFactory.getLog(KalturaProvider.class);

    private AuthCodeProviderService authCodeProviderService;
    public void setAuthCodeProviderService(AuthCodeProviderService authCodeProviderService) {
        this.authCodeProviderService = authCodeProviderService;
    }

    private CKEditorProviderService ckEditorProviderService;
    public void setCkEditorProviderService(CKEditorProviderService ckEditorProviderService) {
        this.ckEditorProviderService = ckEditorProviderService;
    }

    private GitProviderService gitProviderService;
    public void setGitProviderService(GitProviderService gitProviderService) {
        this.gitProviderService = gitProviderService;
    }

    private RoleProviderService roleProviderService;
    public void setRoleProviderService(RoleProviderService roleProviderService) {
        this.roleProviderService = roleProviderService;
    }

    private SecurityService securityService;
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    private UserProviderService userProviderService;
    public void setUserProviderService(UserProviderService userProviderService) {
        this.userProviderService = userProviderService;
    }

    public void init() {
        log.info("INIT: KalturaProvider");
    }

    public final static String PREFIX = "kaltura";
    public String getEntityPrefix() {
        return PREFIX;
    }

    public String[] getHandledInputFormats() {
        return new String[] {
            Formats.JSON
        };
    }

    public String[] getHandledOutputFormats() {
        return new String[] {
            Formats.JSON,
            Formats.HTML
        };
    }

    /*
     * Custom Action methods
     */

    /**
     * The role API
     *
     * GET/POST kaltura/role
     * GET kaltura/role/sakai
     * GET kaltura/role/lti
     * GET/PUT/POST kaltura/role/{roleId}
     * POST kaltura/role/delete/{roleId}
     */
    @EntityCustomAction(action="role", viewKey="")
    public ActionReturn role(EntityView view, Map<String, Object> params) {
        securityService.securityCheck();

        ActionReturn actionReturn = null;

        String id = view.getPathSegment(2);

        if (StringUtils.equalsIgnoreCase(EntityView.Method.POST.name(), view.getMethod()) ||
                StringUtils.equalsIgnoreCase(EntityView.Method.PUT.name(), view.getMethod())) {
            // POST or PUT
            if (!StringUtils.equalsIgnoreCase(id, "delete") && params.get("data") == null) {
                throw new IllegalArgumentException("No data object defined in input");
            }

            if (StringUtils.isNotBlank(id)) {
                if (StringUtils.equalsIgnoreCase(id, "delete")) {
                    // delete the role mapping
                    id = view.getPathSegment(3);

                    if (StringUtils.isBlank(id)) {
                        throw new IllegalArgumentException("No ID specified for delete operation");
                    }

                    actionReturn = roleProviderService.deleteRoleMapping(id);
                } else {
                    // ID given, update role mapping
                    actionReturn = roleProviderService.updateRoleMapping(id, (String) params.get("data"));
                }
            } else {
                // no ID given, add new role mapping
                actionReturn = roleProviderService.addRoleMapping((String) params.get("data"));
            }
        } else if (StringUtils.equalsIgnoreCase(EntityView.Method.GET.name(), view.getMethod())) {
            // GET
            if (StringUtils.equalsIgnoreCase(id, "sakai")) {
                actionReturn = roleProviderService.getAllSakaiRoles();
            } else if (StringUtils.equalsIgnoreCase(id, "lti")) {
                actionReturn = roleProviderService.getAllLtiRoles();
            } else {
                actionReturn = roleProviderService.get(id);
            }
        } else {
            throw new IllegalArgumentException("Method not allowed on kaltura/role: " + view.getMethod());
        }

        return actionReturn;
    }

    /**
     * The user API
     *
     * GET kaltura/user/{userId}
     */
    @EntityCustomAction(action="user", viewKey=EntityView.VIEW_LIST)
    public ActionReturn user(EntityView view, Map<String, Object> params) {
        // GET
        String userId = view.getPathSegment(2);

        securityService.securityCheck((String) params.get("auth_code"), userId);

        ActionReturn actionReturn = userProviderService.get(userId);

        return actionReturn;
    }

    /**
     * The role API
     *
     * POST/PUT kaltura/auth
     * GET kaltura/auth/{authCode}
     */
    @EntityCustomAction(action="auth", viewKey="")
    public ActionReturn auth(EntityView view, Map<String, Object> params) {
        securityService.securityCheck();

        ActionReturn actionReturn = null;

        String code = view.getPathSegment(2);

        if (StringUtils.equalsIgnoreCase(EntityView.Method.POST.name(), view.getMethod()) ||
                StringUtils.equalsIgnoreCase(EntityView.Method.PUT.name(), view.getMethod())) {
            // POST or PUT
            if (params.get("data") == null) {
                throw new IllegalArgumentException("No data object defined in input");
            }

            if (StringUtils.isNotBlank(code)) {
                // INVALID
                throw new IllegalArgumentException("INVALID USAGE: Auth code given on kaltura/auth: " + view.getMethod());
            } else {
                // no ID given, add new auth code
                actionReturn = authCodeProviderService.createAuthCode((String) params.get("data"));
            }
        } else if (StringUtils.equalsIgnoreCase(EntityView.Method.GET.name(), view.getMethod())) {
            // GET
            if (StringUtils.isBlank(code)) {
                // INVALID
                throw new IllegalArgumentException("INVALID USAGE: No auth code given on kaltura/auth: " + view.getMethod());
            } else {
                // code given, get the auth code
                actionReturn = authCodeProviderService.getAuthCode(code);
            }
        } else {
            throw new IllegalArgumentException("Method not allowed on kaltura/auth: " + view.getMethod());
        }

        return actionReturn;
    }

    /**
     * The git API
     *
     * GET kaltura/git
     */
    @EntityCustomAction(action="git", viewKey=EntityView.VIEW_LIST)
    public ActionReturn git(EntityView view, Map<String, Object> params) {
        // GET
        ActionReturn actionReturn = gitProviderService.get();

        return actionReturn;
    }

    /**
     * The ckeditor API
     *
     * GET kaltura/ckeditor/site/{siteId}
     * @throws Exception 
     */
    @EntityCustomAction(action="ckeditor", viewKey=EntityView.VIEW_LIST)
    public ActionReturn ckeditor(EntityView entityView, Map<String, Object> params) throws Exception {
        // GET
        String siteId = entityView.getPathSegment(3);

        if (StringUtils.isBlank(siteId)) {
            throw new IllegalArgumentException("No site ID defined in input");
        }

        ActionReturn actionReturn = ckEditorProviderService.launchLti(siteId);

        return actionReturn;
    }

    /*
     * Inherited methods
     */

    public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        return null;
    }

    public Object getSampleEntity() {
        throw new IllegalArgumentException("Method not allowed on kaltura.");
    }

    public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        throw new IllegalArgumentException("Method not allowed on kaltura.");
    }

    public Object getEntity(EntityReference ref) {
        throw new IllegalArgumentException("Method not allowed on kaltura.");
    }

    public void deleteEntity(EntityReference ref, Map<String, Object> params) {
        throw new IllegalArgumentException("Method not allowed on kaltura.");
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        throw new IllegalArgumentException("Method not allowed on kaltura.");
    }

    public boolean entityExists(String id) {
        throw new IllegalArgumentException("Method not allowed on kaltura.");
    }

}
