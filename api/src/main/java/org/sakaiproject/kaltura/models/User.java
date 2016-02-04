/*
 * Copyright ©2016 Kaltura, Inc.
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
