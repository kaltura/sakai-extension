/**
 * Copyright 2014 Sakaiproject Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.sakaiproject.kaltura.providers;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.kaltura.services.provider.AuthCodeProviderService;
import org.sakaiproject.kaltura.services.provider.RoleProviderService;
import org.sakaiproject.kaltura.services.provider.UserProviderService;
import org.sakaiproject.kaltura.utils.common.SecurityUtil;

/**
 * The Auto Roster provider for the REST API
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaProvider extends AbstractEntityProvider implements RESTful {

    private final Log log = LogFactory.getLog(KalturaProvider.class);

    private AuthCodeProviderService authCodeProviderService;
    public void setAuthCodeProviderService(
            AuthCodeProviderService authCodeProviderService) {
        this.authCodeProviderService = authCodeProviderService;
    }

    private RoleProviderService roleProviderService;
    public void setRoleProviderService(RoleProviderService roleProviderService) {
        this.roleProviderService = roleProviderService;
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
        return new String[] {Formats.JSON};
    }

    public String[] getHandledOutputFormats() {
        return new String[] {Formats.JSON};
    }

    /*
     * Custom Action methods
     */

    /**
     * The role API
     *
     * GET/POST kaltura/role
     * GET/PUT kaltura/role/{roleId}
     * GET kaltura/role/active
     * GET kaltura/role/inactive
     */
    @EntityCustomAction(action="role", viewKey="")
    public ActionReturn role(EntityView view, Map<String, Object> params) {
        SecurityUtil.securityCheck((String) params.get("shared_secret"));

        ActionReturn actionReturn = null;

        String id = view.getPathSegment(2);

        if (StringUtils.equalsIgnoreCase(EntityView.Method.POST.name(), view.getMethod()) ||
                StringUtils.equalsIgnoreCase(EntityView.Method.PUT.name(), view.getMethod())) {
            // POST or PUT
            if (params.get("data") == null) {
                throw new IllegalArgumentException("No data object defined in input");
            }

            if (StringUtils.isNotBlank(id)) {
                // ID given, update role mapping
                actionReturn = roleProviderService.updateRoleMapping((String) params.get("data"));
            } else {
                // no ID given, add new role mapping
                actionReturn = roleProviderService.addRoleMapping((String) params.get("data"));
            }
        } else if (StringUtils.equalsIgnoreCase(EntityView.Method.GET.name(), view.getMethod())) {
            // GET
            if (StringUtils.equalsIgnoreCase(id, "active")) {
                actionReturn = roleProviderService.getActiveRoles();
            } else if (StringUtils.equalsIgnoreCase(id, "inactive")) {
                actionReturn = roleProviderService.getInactiveRoles();
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
        SecurityUtil.securityCheck((String) params.get("shared_secret"));

        ActionReturn actionReturn = null;

        // GET
        String userId = view.getPathSegment(2);
        actionReturn = userProviderService.get(userId);

        return actionReturn;
    }

    /**
     * The role API
     *
     * POST kaltura/auth
     * GET kaltura/auth/{authCode}
     * POST/PUT kaltura/auth/inactivate/{authCode}
     */
    @EntityCustomAction(action="auth", viewKey="")
    public ActionReturn auth(EntityView view, Map<String, Object> params) {
        SecurityUtil.securityCheck((String) params.get("shared_secret"));

        ActionReturn actionReturn = null;

        String code = view.getPathSegment(2);

        if (StringUtils.equalsIgnoreCase(EntityView.Method.POST.name(), view.getMethod()) ||
                StringUtils.equalsIgnoreCase(EntityView.Method.PUT.name(), view.getMethod())) {
            // POST or PUT
            if (StringUtils.equalsIgnoreCase("inactivate", code)) {
                // inactivate code
                code = view.getPathSegment(3);
                if (StringUtils.isBlank(code)) {
                    throw new IllegalArgumentException("No auth code given kaltura/auth/inactivate: " + view.getMethod());
                }
                actionReturn = authCodeProviderService.inactivateAuthCode(code);
            } else {
                
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

    /*
     * Inherited methods
     */

    public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        return null;
    }

    public Object getSampleEntity() {
        throw new IllegalArgumentException("Method not allowed on auto-roster.");
    }

    public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        throw new IllegalArgumentException("Method not allowed on auto-roster.");
    }

    public Object getEntity(EntityReference ref) {
        throw new IllegalArgumentException("Method not allowed on auto-roster.");
    }

    public void deleteEntity(EntityReference ref, Map<String, Object> params) {
        throw new IllegalArgumentException("Method not allowed on auto-roster.");
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        throw new IllegalArgumentException("Method not allowed on auto-roster.");
    }

    public boolean entityExists(String id) {
        throw new IllegalArgumentException("Method not allowed on auto-roster.");
    }

}
