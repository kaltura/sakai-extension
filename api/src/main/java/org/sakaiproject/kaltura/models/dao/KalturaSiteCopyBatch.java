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
package org.sakaiproject.kaltura.models.dao;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.utils.JsonUtil;

import com.google.gson.annotations.Expose;

/**
 * This is a Kaltura LTI RESTful API authorization, it represents an authorization code object from the database
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaSiteCopyBatch implements Serializable{

    private static final long serialVersionUID = 1L;
    public static final String NEW_STATUS = "new";
    public static final String IN_PROGRESS_STATUS = "in progress";
    public static final String COMPLETE_STATUS = "complete";
    public static final String FAILED_STATUS = "failed";
    public static final String REMOVED_STATUS = "removed";

    private Long batchId ;
    private String sourceSiteId;
    private String targetSiteId;
    private String status;
    private int attempts =0;
    private Date dateCreated;

    public KalturaSiteCopyBatch() {
    }

    public KalturaSiteCopyBatch(String sourceSiteId , String targetSiteId, String status){
        this.sourceSiteId = sourceSiteId;
        this.targetSiteId = targetSiteId;
        this.status = status;
        this.dateCreated = new Date();        
    }

    /**
     * Convenience constructor to ensure a valid {@link KalturaLtiRole} object exists
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object
     */
    public KalturaSiteCopyBatch(KalturaSiteCopyBatch kalturaSiteCopyBatch) {
        this(
                kalturaSiteCopyBatch.getSourceSiteId(),
                kalturaSiteCopyBatch.getTargetSiteId(),
                kalturaSiteCopyBatch.getStatus()
            );
    }

    /**
     * Override to show this model as a JSON string
     */
    @Override
    public String toString() {
        return JsonUtil.parseToJson(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KalturaSiteCopyBatch other = (KalturaSiteCopyBatch) obj;
        if (batchId == null || other.batchId == null)  {
            return false;
        } else {
            return batchId.equals(other.batchId); // use id only if set
        }
    }

    /**
     * Is this a fully-constructed {@link KalturaLtiRole} object?
     * 
     * @return true, if all required fields are valid
     */
    public boolean isValid() {
        if (StringUtils.isBlank(sourceSiteId)) {
            return false;
        }
        if (StringUtils.isBlank(targetSiteId)) {
            return false;
        }
        if (dateCreated == null) {
            return false;
        }
        if (StringUtils.isBlank(status)) {
            return false;
        }

        return true;
    }

    public Long getBatchId() {
        return batchId;
    }
    public void setBatchId(Long id) {
        this.batchId = id;
    }

    public String getSourceSiteId() {
        return sourceSiteId;
    }
    public void setSourceSiteId(String sourceSiteId) {
        this.sourceSiteId = sourceSiteId;
    }

    public String getTargetSiteId() {
        return targetSiteId;
    }
    public void setTargetSiteId(String targetSiteId) {
        this.targetSiteId = targetSiteId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public void setAttempts(int newAttempts) {
        attempts = newAttempts;
    }

    public int getAttempts() {
        return attempts;
    }

    public Date getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}
