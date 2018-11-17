/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.kaltura.util.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {

    private String id;
    private String eid;

    @JsonIgnore
    private org.sakaiproject.user.api.User sakaiUser;

    /**
     * List of the user's sites and LTI role in each site
     */
    @JsonProperty("memberships")
    private List<UserSiteRole> userSiteRoles;

    public User(org.sakaiproject.user.api.User sakaiUser) {
        this(sakaiUser, new ArrayList<>());
    }

    public User(org.sakaiproject.user.api.User sakaiUser, List<UserSiteRole> userSiteRoles) {
        this.sakaiUser = sakaiUser;
        this.userSiteRoles = userSiteRoles;

        if (this.sakaiUser != null) {
            this.id = sakaiUser.getId();
            this.eid = sakaiUser.getEid();
        }
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
