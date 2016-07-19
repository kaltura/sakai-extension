/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.dao;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.Constants;

/**
 * This is a Kaltura Site Copy Job, it represents an job that is added on sakai to check the status of kaltura jobs created for given site copy request
 * 
 * @author Esh Nagappan (ynagappan @ unicon.net)
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

    public KalturaSiteCopyJob(Long jobId, Long batchId, Long kalturaJobId, String status, int attempts, Date dateCreated) {
        this.jobId = jobId;
        this.batchId = batchId;
        this.kalturaJobId = kalturaJobId;
        this.status = status;
        this.attempts = attempts;
        this.dateCreated = dateCreated;
    }

    public KalturaSiteCopyJob(Long batchId, Long kalturaJobId, String status){
        this.batchId = batchId;
        this.kalturaJobId = kalturaJobId;
        this.status = status;
        this.dateCreated = new Date();
    }

    /**
     * Convenience constructor to ensure a valid {@link KalturaSiteCopyJob} object exists
     * 
     * @param kalturaSiteCopyJob the {@link KalturaSiteCopyJob} object
     */
    public KalturaSiteCopyJob(KalturaSiteCopyJob kalturaSiteCopyJob) {
        this(
                kalturaSiteCopyJob.getBatchId(),
                kalturaSiteCopyJob.getKalturaJobId(),
                kalturaSiteCopyJob.getStatus()
            );
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[KalturaSiteCopyJob:");
        output.append(jobId);
        output.append(',');
        output.append(batchId);
        output.append(',');
        output.append(kalturaJobId);
        output.append(',');
        output.append(attempts);
        output.append(',');
        output.append(status);
        output.append(']');
        return output.toString();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KalturaSiteCopyJob))
            return false;
        if (this == obj)
            return true;

        KalturaSiteCopyJob other = (KalturaSiteCopyJob) obj;
        if (this.jobId == null || other.jobId == null)  {
            return false;
        } else {
            return this.jobId.equals(other.jobId); // use id only if set
        }
    }

    @Override
    public int hashCode() {
        return jobId.intValue();
    }

    /**
     * Is this a fully-constructed {@link KalturaSiteCopyJob} object?
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
