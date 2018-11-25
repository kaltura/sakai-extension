/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services.provider;

import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.kaltura.services.RestService;
import org.sakaiproject.kaltura.util.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* A spring controlled bean that will be injected
* with properties about the repository state at build time.
* This information is supplied by my plugin - <b>pl.project13.maven.git-commit-id-plugin</b>
*/
@Data
@NoArgsConstructor
public class GitProviderService {
    @JsonIgnore
    @Setter
    private RestService restService;

    private String tags;                    // =${git.tags} // comma separated tag names
    private String branch;                  // =${git.branch}
    private String dirty;                   // =${git.dirty}
    private String remoteOriginUrl;         // =${git.remote.origin.url}
    private String commitId;                // =${git.commit.id.full}
    private String commitIdAbbrev;          // =${git.commit.id.abbrev}
    private String describe;                // =${git.commit.id.describe}
    private String describeShort;           // =${git.commit.id.describe-short}
    private String commitUserName;          // =${git.commit.user.name}
    private String commitUserEmail;         // =${git.commit.user.email}
    private String commitMessageFull;       // =${git.commit.message.full}
    private String commitMessageShort;      // =${git.commit.message.short}
    private String commitTime;              // =${git.commit.time}
    private String closestTagName;          // =${git.closest.tag.name}
    private String closestTagCommitCount;   // =${git.closest.tag.commit.count}
    private String buildUserName;           // =${git.build.user.name}
    private String buildUserEmail;          // =${git.build.user.email}
    private String buildTime;               // =${git.build.time}
    private String buildHost;               // =${git.build.host}
    private String buildVersion;            // =${git.build.version}

    /**
     * Gets the git revision information
     */
    public ActionReturn get() {
        return restService.processActionReturn(JsonUtil.parseToJson(this));
    }
}
