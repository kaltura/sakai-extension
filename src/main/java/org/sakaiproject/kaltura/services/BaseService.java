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
package org.sakaiproject.kaltura.services;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.kaltura.models.errors.BaseError;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;

/**
 * Handles service injections, DAOs, and configuration options
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class BaseService {

    /*
     * App services
     */

    /**
     * {@link RoleService}
     */
    private RoleService roleService;
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
    public RoleService getRoleService() {
        return roleService;
    }

    /**
     * {@link UserService}
     */
    private UserService userService;
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public UserService getUserService() {
        return userService;
    }

    /*
     * Sakai Services
     */

    /**
     * {@link AuthzGroupService}
     */
    protected AuthzGroupService authzGroupService;
    public void setAuthzGroupService(AuthzGroupService authzGroupService) {
        this.authzGroupService = authzGroupService;
    }
    public AuthzGroupService getAuthzGroupService() {
        return authzGroupService;
    }

    /**
     * {@link SiteService}
     */
    protected SiteService siteService;
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
    public SiteService getSiteService() {
        return siteService;
    }

    /**
     * {@link ServerConfigurationService}
     */
    protected ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }
    public ServerConfigurationService getServerConfigurationService() {
        return serverConfigurationService;
    }

    /**
     * {@link SqlService}
     */
    protected SqlService sqlService;
    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }
    public SqlService getSqlService() {
        return sqlService;
    }

    /**
     * {@link UserDirectoryService}
     */
    protected UserDirectoryService userDirectoryService;
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }
    public UserDirectoryService getUserDirectoryService() {
        return userDirectoryService;
    }

    /*
     * DAO
     */

    /*
     * Configuration settings
     */

    /**
     * Convenience method to calculate the return data and the response HTTP code
     * 
     * @param errors the BaseError object containing any errors
     * @return the ActionReturn with the HTTP code and any errors
     */
    public ActionReturn processActionReturn(BaseError errors) {
        return processActionReturn(errors, null);
    }

    /**
     * Convenience method to calculate the return data and the response HTTP code
     * 
     * @param data the data to be returned
     * @return the ActionReturn with the HTTP code and any data
     */
    public ActionReturn processActionReturn(String data) {
        return processActionReturn(null, data);
    }

    /**
     * Calculates the return data and the response HTTP code
     * 
     * @param errors the BaseError object containing any errors
     * @param data the data to be returned
     * @return the ActionReturn with the HTTP code and any errors or data
     */
    public ActionReturn processActionReturn(BaseError errors, String data) {
        String rv = null;
        int responseCode = -1;

        if ((errors == null || errors.isEmpty()) && StringUtils.isBlank(data)) {
            // nothing to return
            responseCode = 204;
        } else if ((errors == null || errors.isEmpty()) && StringUtils.isNotBlank(data)) {
            // data to return
            rv = data;
            responseCode = 200;
        } else if (errors != null && !errors.isEmpty()) {
            // errors to return
            rv = errors.toString();
            responseCode = 400;
        }

        ActionReturn actionReturn = new ActionReturn("UTF-8", Formats.JSON, rv);
        actionReturn.setResponseCode(responseCode);

        return actionReturn;
    }
}
