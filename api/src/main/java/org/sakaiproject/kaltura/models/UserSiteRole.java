/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models;

import org.sakaiproject.kaltura.utils.JsonUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserSiteRole {

    /**
     * The ID of the site
     */
    @Expose
    @SerializedName("context_id")
    private String contextId;

    /**
     * The title of the site
     */
    @Expose
    @SerializedName("context_title")
    private String contextTitle;

    /**
     * A comma-separated string with the LTI roles
     */
    @Expose
    private String ltiRoles;

    public UserSiteRole(){}

    public UserSiteRole(String contextId) {
        this(contextId, null);
    }

    public UserSiteRole(String contextId, String ltiRoles) {
        this.contextId = contextId;
        this.ltiRoles = ltiRoles;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getContextTitle() {
        return contextTitle;
    }

    public void setContextTitle(String contextTitle) {
        this.contextTitle = contextTitle;
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
