/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models;

import org.sakaiproject.kaltura.util.JsonUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSiteRole {

    /**
     * The ID of the site
     */
    @JsonProperty("context_id")
    private String contextId;

    /**
     * The title of the site
     */
    @JsonProperty("context_title")
    private String contextTitle;

    /**
     * A comma-separated string with the LTI roles
     */
    private String ltiRoles;

    public UserSiteRole(String contextId) {
        this(contextId, null);
    }

    public UserSiteRole(String contextId, String ltiRoles) {
        this.contextId = contextId;
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
