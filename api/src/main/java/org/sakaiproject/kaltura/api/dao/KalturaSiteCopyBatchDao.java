/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.api.dao;

import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;

import java.util.Optional;

/**
 * DAO Interface for Kaltura Site Copy Details
 * 
 * @author Esh Nagappan (ynagappan @ unicon.net)
 */
public interface KalturaSiteCopyBatchDao {

    /**
     * Check the work queue for any Kaltura Site Copy Batch job with new status 
     * 
     * @return {@link KalturaSiteCopyBatch) object
     */
    public Optional<KalturaSiteCopyBatch> checkWorkQueue(String status);

    /**
     * Get the kaltura site copy batch object related to batch id
     * 
     * @param batchId the batch ID
     * @return the {@link KalturaSiteCopyBatch} object
     */
    public KalturaSiteCopyBatch getSiteCopyBatch(Long batchId);

    /**
     * Add/update a new kaltura site copy batch object
     * 
     * @param kalturaSiteCopyBatch the {@link KalturaSiteCopyBatch} object to add
     * @param update  boolean - true to update existing object, false for creation
     * @return long - batchId of record if added/updated successfully
     */
    public Long save(KalturaSiteCopyBatch kalturaSiteCopyBatch, boolean update);
}
