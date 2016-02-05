/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.impl.dao.jdbc.data.SiteCopyBatchData;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;

/**
 * Implementation of DAO Interface for Kaltura Site Copy Batch 
 * 
 * @author Esh Nagappan (ynagappan @ unicon.net)
 */
public class KalturaSiteCopyBatchDaoImpl implements KalturaSiteCopyBatchDao {

    private static final Log log = LogFactory.getLog(KalturaSiteCopyBatchDaoImpl.class);

    private SiteCopyBatchData siteCopyBatchData;
    public void setSiteCopyBatchData(SiteCopyBatchData siteCopyBatchData) {
        this.siteCopyBatchData = siteCopyBatchData;
    }

    /**
     * Check the work queue for any Kaltura Site Copy Batch job with new status 
     * 
     * @return {@link KalturaSiteCopyBatch) object
     */
    public KalturaSiteCopyBatch checkWorkQueue(String status){
        List<KalturaSiteCopyBatch> jobs = siteCopyBatchData.getJobs(status);

        log.debug("Checked Kaltura Site Copy Batch work queue. Found rows: " + jobs.size());

        for (KalturaSiteCopyBatch job : jobs) {
            // will return the first job
            return job;
        }

        return null;
    }

    /**
    * Get the kaltura site copy batch object related to batch id
    * 
    * @param batchId the batch ID
    * @return the {@link KalturaSiteCopyBatch} object
    */
    public KalturaSiteCopyBatch getSiteCopyBatch(Long batchId){
        KalturaSiteCopyBatch kalturaSiteCopyBatch = siteCopyBatchData.getJob(batchId);

        if (kalturaSiteCopyBatch == null) {
            log.debug("Checked Kaltura Site Copy Batch table. Found no rows.");
        } else {
            log.debug("Checked Kaltura Site Copy Batch table. Found row for batchId: " + batchId.toString());
            return kalturaSiteCopyBatch;
        }

        return null;
    }
    
    /**
    * Add/update a new kaltura site copy batch object
    * 
    * @param kalturaSiteCopyBatch the {@link KalturaSiteCopyBatch} object to add
    * @return true, if added/updated successfully
    */
    public Long save(KalturaSiteCopyBatch batch, boolean update){
        if (!batch.isValid()) {
            log.error("Batch details are not valid. Could not create kaltura site copy batch:" + batch.toString());
        }

        Long batchId = siteCopyBatchData.persistBatch(batch, update);
        if (!update && batchId == null) {
            log.error("Could not persist kaltura site copy batch.");
        } else {
            log.info("Persisted kaltura site copy batch row:" + batchId);
        }

        return batchId;
    }

}
