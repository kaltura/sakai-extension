/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services.provider;

import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.services.RestService;
import org.sakaiproject.kaltura.utils.JsonUtil;

import com.google.gson.annotations.Expose;

/**
* A spring controlled bean that will be injected
* with properties about the repository state at build time.
* This information is supplied by my plugin - <b>pl.project13.maven.git-commit-id-plugin</b>
*/
public class GitProviderService {
    @Expose
    private String tags;                    // =${git.tags} // comma separated tag names
    @Expose
    private String branch;                  // =${git.branch}
    @Expose
    private String dirty;                   // =${git.dirty}
    @Expose
    private String remoteOriginUrl;         // =${git.remote.origin.url}
    @Expose
    private String commitId;                // =${git.commit.id.full}
    @Expose
    private String commitIdAbbrev;          // =${git.commit.id.abbrev}
    @Expose
    private String describe;                // =${git.commit.id.describe}
    @Expose
    private String describeShort;           // =${git.commit.id.describe-short}
    @Expose
    private String commitUserName;          // =${git.commit.user.name}
    @Expose
    private String commitUserEmail;         // =${git.commit.user.email}
    @Expose
    private String commitMessageFull;       // =${git.commit.message.full}
    @Expose
    private String commitMessageShort;      // =${git.commit.message.short}
    @Expose
    private String commitTime;              // =${git.commit.time}
    @Expose
    private String closestTagName;          // =${git.closest.tag.name}
    @Expose
    private String closestTagCommitCount;   // =${git.closest.tag.commit.count}
    @Expose
    private String buildUserName;           // =${git.build.user.name}
    @Expose
    private String buildUserEmail;          // =${git.build.user.email}
    @Expose
    private String buildTime;               // =${git.build.time}
    @Expose
    private String buildHost;               // =${git.build.host}
    @Expose
    private String buildVersion;            // =${git.build.version}

    private RestService restService;
    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    public GitProviderService() {
    }

    /**
     * Gets the git revision information
     */
    public ActionReturn get() {
        return restService.processActionReturn(JsonUtil.parseToJson(this));
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDirty() {
        return dirty;
    }

    public void setDirty(String dirty) {
        this.dirty = dirty;
    }

    public String getRemoteOriginUrl() {
        return remoteOriginUrl;
    }

    public void setRemoteOriginUrl(String remoteOriginUrl) {
        this.remoteOriginUrl = remoteOriginUrl;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitIdAbbrev() {
        return commitIdAbbrev;
    }

    public void setCommitIdAbbrev(String commitIdAbbrev) {
        this.commitIdAbbrev = commitIdAbbrev;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDescribeShort() {
        return describeShort;
    }

    public void setDescribeShort(String describeShort) {
        this.describeShort = describeShort;
    }

    public String getCommitUserName() {
        return commitUserName;
    }

    public void setCommitUserName(String commitUserName) {
        this.commitUserName = commitUserName;
    }

    public String getCommitUserEmail() {
        return commitUserEmail;
    }

    public void setCommitUserEmail(String commitUserEmail) {
        this.commitUserEmail = commitUserEmail;
    }

    public String getCommitMessageFull() {
        return commitMessageFull;
    }

    public void setCommitMessageFull(String commitMessageFull) {
        this.commitMessageFull = commitMessageFull;
    }

    public String getCommitMessageShort() {
        return commitMessageShort;
    }

    public void setCommitMessageShort(String commitMessageShort) {
        this.commitMessageShort = commitMessageShort;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    public String getClosestTagName() {
        return closestTagName;
    }

    public void setClosestTagName(String closestTagName) {
        this.closestTagName = closestTagName;
    }

    public String getClosestTagCommitCount() {
        return closestTagCommitCount;
    }

    public void setClosestTagCommitCount(String closestTagCommitCount) {
        this.closestTagCommitCount = closestTagCommitCount;
    }

    public String getBuildUserName() {
        return buildUserName;
    }

    public void setBuildUserName(String buildUserName) {
        this.buildUserName = buildUserName;
    }

    public String getBuildUserEmail() {
        return buildUserEmail;
    }

    public void setBuildUserEmail(String buildUserEmail) {
        this.buildUserEmail = buildUserEmail;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public String getBuildHost() {
        return buildHost;
    }

    public void setBuildHost(String buildHost) {
        this.buildHost = buildHost;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

}
