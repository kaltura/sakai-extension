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
import com.google.gson.annotations.SerializedName;

public class UserSiteRole {

    /**
     * The ID of the site
     */
    @Expose
    @SerializedName("context_id")
    private String siteId;

    /**
     * The title of the site
     */
    @Expose
    @SerializedName("context_title")
    private String siteTitle;

    /**
     * A comma-separated string with the LTI roles
     */
    @Expose
    private String ltiRoles;

    public UserSiteRole(){}

    public UserSiteRole(String siteId) {
        this(siteId, null);
    }

    public UserSiteRole(String siteId, String ltiRoles) {
        this.siteId = siteId;
        this.ltiRoles = ltiRoles;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getLtiRoles() {
        return ltiRoles;
    }

    public void setLtiRoles(String ltiRoles) {
        this.ltiRoles = ltiRoles;
    }

    /**
     * Add an LTI role to the comma-delimited string
     * 
     * @param ltiRole the LTI role to add
     */
    public void addLtiRole(String ltiRole) {
        this.ltiRoles += "," + ltiRole;
    }

    /**
     * Override to show this model as a JSON string
     */
    @Override
    public String toString() {
        return JsonUtil.parseToJson(this);
    }

}
