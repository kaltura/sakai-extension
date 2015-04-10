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

import com.google.gson.annotations.Expose;

public class User {

    @Expose
    private String id;
    @Expose
    private String eid;
    @Expose
    private String displayName;
    @Expose
    private String email;
    @Expose
    private String type;

    /**
     * The Sakai {@link org.sakaiproject.user.api.User} object
     */
    private org.sakaiproject.user.api.User sakaiUser;

    /**
     * List of the user's sites and LTI role in each site
     */
    @Expose
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
            this.email = sakaiUser.getEmail();
            this.type = sakaiUser.getType();
            this.displayName = sakaiUser.getDisplayName();
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
