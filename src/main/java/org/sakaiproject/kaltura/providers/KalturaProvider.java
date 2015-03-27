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
import org.sakaiproject.kaltura.services.BaseService;
import org.sakaiproject.kaltura.utils.common.SecurityUtil;

/**
 * The Auto Roster provider for the REST API
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaProvider extends AbstractEntityProvider implements RESTful {

    private final Log log = LogFactory.getLog(KalturaProvider.class);

    private BaseService baseService;
    public void setBaseService(BaseService baseService) {
        this.baseService = baseService;
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
     * kaltura/role
     * kaltura/role/{roleId}
     */
    @EntityCustomAction(action="role", viewKey="")
    public ActionReturn role(EntityView view, Map<String, Object> params) {
        SecurityUtil.securityCheck((String) params.get("shared_secret"));

        ActionReturn actionReturn = null;

        if (StringUtils.equalsIgnoreCase(EntityView.Method.POST.name(), view.getMethod()) ||
                StringUtils.equalsIgnoreCase(EntityView.Method.PUT.name(), view.getMethod())) {
            // POST or PUT
            if (params.get("data") == null) {
                throw new IllegalArgumentException("No data object defined in input");
            }
        } else if (StringUtils.equalsIgnoreCase(EntityView.Method.GET.name(), view.getMethod())) {
            // GET
            String roleId = view.getPathSegment(2);
            actionReturn = baseService.getRoleService().get(roleId);
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
        actionReturn = baseService.getUserService().get(userId);

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
