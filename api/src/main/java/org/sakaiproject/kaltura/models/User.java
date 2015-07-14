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

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.kaltura.utils.JsonUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @Expose
    private String id;

    @Expose
    private String eid;

    /**
     * The Sakai {@link org.sakaiproject.kaltura.api.dao.models.user.api.User} object
     */
    private org.sakaiproject.user.api.User sakaiUser;

    /**
     * List of the user's sites and LTI role in each site
     */
    @Expose
    @SerializedName("memberships")
    private List<UserSiteRole> userSiteRoles;

    public User(org.sakaiproject.user.api.User sakaiUser) {
        this(sakaiUser, new ArrayList<UserSiteRole>());
    }

    public User(org.sakaiproject.user.api.User sakaiUser, List<UserSiteRole> userSiteRoles) {
        this.sakaiUser = sakaiUser;
        this.userSiteRoles = userSiteRoles;

        if (this.sakaiUser != null) {
            this.id = sakaiUser.getId();
            this.eid = sakaiUser.getEid();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public org.sakaiproject.user.api.User getSakaiUser() {
        return sakaiUser;
    }

    public void setSakaiUser(org.sakaiproject.user.api.User sakaiUser) {
        this.sakaiUser = sakaiUser;
    }

    public List<UserSiteRole> getUserSiteRoles() {
        return userSiteRoles;
    }

    public void setUserSiteRoles(List<UserSiteRole> userSiteRoles) {
        this.userSiteRoles = userSiteRoles;
    }

    public void addUserSiteRole(UserSiteRole userSiteRole) {
        if (userSiteRoles == null) {
            userSiteRoles = new ArrayList<UserSiteRole>();
        }

        userSiteRoles.add(userSiteRole);
    }

    /**
     * Override to show this model as a JSON string
     */
    @Override
    public String toString() {
        return JsonUtil.parseToJson(this);
    }

}
