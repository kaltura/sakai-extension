/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.api.dao;

import java.util.List;

import org.hibernate.Transaction;
import org.sakaiproject.genericdao.api.GeneralGenericDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;

/**
 * DAO Interface for Kaltura Site Copy Job
 * 
 * @author Esh Nagappan (ynagappan @ unicon.net)
 */
public interface KalturaSiteCopyJobDao{

    /**
     * Check the work queue for any kaltura site copy job with the given status
     *
     * @param status - job status to check  
     * @return KalturaSiteCopyJob {@link KalturaSiteCopyJob} object
     */
    public KalturaSiteCopyJob checkWorkQueue(String status);

    /**
     * Get the kaltura site copy job associated with the job Id
     * 
     * @param jobId the site copy job ID
     * @return the {@link KalturaSiteCopyJob} object
     */
    public KalturaSiteCopyJob getSiteCopyJob(Long jobId);

    /**
     *Get the list of Kaltura site copy jobs associated with the batch ID
     *@param batchId - batch Id associated with kaltura site copy job
     *@return List of KalturaSiteCopyJob {@link KalturaSiteCopyJob} objects
     */
    public List<KalturaSiteCopyJob> getAllJobs(Long batchId);

    /**
     * Add/update a new kaltura site copy job
     * 
     * @param kalturaSiteCopyJob the {@link KalturaSiteCopyJob} object to add
     * @param update - boolean set to true to indicate its update operation and false for creation of new record
     * @return Long - job Id of record , if added/updated successfully
     */
    public Long save(KalturaSiteCopyJob kalturaSiteCopyJob, boolean update);
}
