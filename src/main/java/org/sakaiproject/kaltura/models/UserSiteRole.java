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
package org.sakaiproject.kaltura.models;

import org.sakaiproject.kaltura.utils.common.JsonUtil;

import com.google.gson.annotations.Expose;

public class UserSiteRole {

    @Expose
    private String siteId;
    @Expose
    private String ltiRole;

    public UserSiteRole(){}

    public UserSiteRole(String siteId) {
        this(siteId, null);
    }

    public UserSiteRole(String siteId, String ltiRole) {
        this.siteId = siteId;
        this.ltiRole = ltiRole;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getLtiRole() {
        return ltiRole;
    }

    public void setLtiRole(String ltiRole) {
        this.ltiRole = ltiRole;
    }

    /**
     * Override to show this model as a JSON string
     */
    @Override
    public String toString() {
        return JsonUtil.parseToJson(this);
    }

}
