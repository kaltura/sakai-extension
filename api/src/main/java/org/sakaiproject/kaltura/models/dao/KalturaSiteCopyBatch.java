/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.dao;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.Constants;


/**
 * This is a Kaltura Site copy batch , it represents an site copy request from sakai to kaltura
 * 
 * @author Esh Nagappan (ynagappan @ unicon.net)
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


    public KalturaSiteCopyBatch(Long batchId, String sourceSiteId, String targetSiteId, String status, int attempts, Date dateCreated) {
        this.batchId = batchId;
        this.sourceSiteId = sourceSiteId;
        this.targetSiteId = targetSiteId;
        this.status = status;
        this.attempts = attempts;
        this.dateCreated = dateCreated;
    }


    public KalturaSiteCopyBatch(String sourceSiteId , String targetSiteId, String status){
        this.sourceSiteId = sourceSiteId;
        this.targetSiteId = targetSiteId;
        this.status = status;
        this.dateCreated = new Date();        
    }

    /**
     * Convenience constructor to ensure a valid {@link KalturaSiteCopyBatch} object exists
     * 
     * @param kalturaSiteCopyBatch the {@link KalturaSiteCopyBatch} object
     */
    public KalturaSiteCopyBatch(KalturaSiteCopyBatch kalturaSiteCopyBatch) {
        this(
                kalturaSiteCopyBatch.getSourceSiteId(),
                kalturaSiteCopyBatch.getTargetSiteId(),
                kalturaSiteCopyBatch.getStatus()
            );
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[KalturaSiteCopyBatch:");
        output.append(batchId);
        output.append(',');
        output.append(sourceSiteId);
        output.append(',');
        output.append(targetSiteId);
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
     * Is this a fully-constructed {@link KalturaSiteCopyBatch) object?
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
