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
public class KalturaSiteCopyJob implements Serializable{

    private static final long serialVersionUID = 1L;

    public static final String NEW_STATUS = "new";
    public static final String IN_PROGRESS_STATUS = "in progress";
    public static final String COMPLETE_STATUS = "complete";
    public static final String FAILED_STATUS = "failed";
    public static final String REMOVED_STATUS = "removed";

    private Long jobId;
    private Long batchId;
    private Long kalturaJobId;
    private String status;
    private int attempts =0;
    private Date dateCreated;

    public KalturaSiteCopyJob() {
    }

    public KalturaSiteCopyJob(Long batchId, Long kalturaJobId, String status){
        this.batchId = batchId;
        this.kalturaJobId = kalturaJobId;
        this.status = status;
        this.dateCreated = new Date();
    }

    public KalturaSiteCopyJob(KalturaSiteCopyJob kalturaSiteCopyJob) {
        this(
                kalturaSiteCopyJob.getBatchId(),
                kalturaSiteCopyJob.getKalturaJobId(),
                kalturaSiteCopyJob.getStatus()
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
        KalturaSiteCopyJob other = (KalturaSiteCopyJob) obj;
        if (jobId == null || other.jobId == null)  {
            return false;
        } else {
            return jobId.equals(other.jobId); // use id only if set
        }
    }

    /**
     * Is this a fully-constructed {@link KalturaLtiRole} object?
     * 
     * @return true, if all required fields are valid
     */
    public boolean isValid() {
        if (kalturaJobId == null) {
            return false;
        }
        if(batchId == null) {
            return false;
        }
        if (StringUtils.isBlank(status)) {
            return false;
        }
        if (dateCreated == null) {
            return false;
        }

        return true;
    }



    public Long getJobId() {
        return jobId;
    }
    public void setJobId(Long id) {
        this.jobId = id;
    }

    public Long getBatchId() {
        return batchId;
    }
    public void setBatchId(Long id) {
        this.batchId = id;
    }

    public Long getKalturaJobId() {
        return kalturaJobId;
    }
    public void setKalturaJobId(Long id) {
        this.kalturaJobId = id;
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
