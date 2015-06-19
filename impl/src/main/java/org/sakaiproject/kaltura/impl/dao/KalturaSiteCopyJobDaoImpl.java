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
package org.sakaiproject.kaltura.impl.dao;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;

/**
 * Implementation of DAO Interface for Kaltura Site Copy Batch 
 * 
 * @author Esh Nagappan (ynagappan @ unicon.net)
 */
public class KalturaSiteCopyJobDaoImpl implements KalturaSiteCopyJobDao {

    private static final Log log = LogFactory.getLog(KalturaSiteCopyJobDaoImpl.class);

    protected SqlService sqlService = null;
   
    protected static final KalturaSiteCopyJobReader READER = new KalturaSiteCopyJobReader();

    protected static final String FIND_NEW_JOBS_SQL = "SELECT job_id, batch_id,kaltura_job_id,status,attempts,created_on FROM kaltura_site_copy_job WHERE status=? ORDER BY attempts,job_id";

    protected static final String SELECT_SQL = "SELECT job_id, kaltura_job_id, batch_id,status,attempts,created_on FROM kaltura_site_copy_job WHERE job_id=?";

    protected static final String SELECT_BATCH_JOBS_SQL = "SELECT job_id, kaltura_job_id, batch_id,status,attempts,created_on FROM kaltura_site_copy_job WHERE batch_id=?";

    private static final String NEW_JOB_SQL = "INSERT INTO kaltura_site_copy_job(batch_id,kaltura_job_id,status, attempts, created_on) VALUES (?, ?, ?, ?, NOW())";

    private static final String UPDATE_JOB_SQL = "UPDATE kaltura_site_copy_job SET batch_id=?, kaltura_job_id=?, status=?, attempts=? WHERE job_id=? ";

    /**
     * Check the work queue for any Kaltura Site Copy Batch job with new status 
     * 
     * @return {@link KalturaSiteCopyBatch) object
     */
    public KalturaSiteCopyJob checkWorkQueue(String status){
        Object fields[] = null;
        fields = new Object[1];
        fields[0] = status;
        List<?> rows = null;
        rows = sqlService.dbRead(FIND_NEW_JOBS_SQL, fields, READER);
        if (rows == null) {
            log.debug("Checked Kaltura Site Copy Job work queue. Found no rows.");
        } else {
            log.debug("Checked Kaltura Site Copy Job work queue. Found rows: " + rows.size());
        }

        if ((rows != null) && (rows.size() > 0)){
            KalturaSiteCopyJob job = (KalturaSiteCopyJob)rows.get(0);
            return job;
        }

        return null;
    }

    /**
    * Get the kaltura site copy job object related to job id
    * 
    * @param jobId the job ID
    * @return the {@link KalturaSiteCopyJob} object
    */
    public KalturaSiteCopyJob getSiteCopyJob(Long jobId){
        
        if (jobId == null) {
            throw new IllegalArgumentException("Job ID cannot be blank.");
        }
        Object fields[] = null;
        fields = new Object[1];
        fields[0] = jobId;
        List<?> rows = null;
        rows = sqlService.dbRead(SELECT_SQL, fields, READER);
        if (rows == null) {
            log.debug("Checked Kaltura Site Copy Job table. Found no rows.");
        } else {
            log.debug("Checked Kaltura Site Copy Job table. Found row for jobId: " + jobId.toString());
            
        }
        if ((rows != null) && (rows.size() > 0)){
            KalturaSiteCopyJob job = (KalturaSiteCopyJob)rows.get(0);
            return job;
        }
        return null;
    }

    /**
     *Get the list of Kaltura site copy jobs associated with the batch ID
     *@param batchId - batch Id associated with kaltura site copy job
     *@return List of KalturaSiteCopyJob {@link KalturaSiteCopyJob} objects
     */
    public List<KalturaSiteCopyJob> getAllJobs(Long batchId){

        if (batchId == null) {
            throw new IllegalArgumentException("Batch ID cannot be blank.");
        }
        Object fields[] = null;
        fields = new Object[1];
        fields[0] = batchId;
        List<?> rows = null;
        List<KalturaSiteCopyJob> jobs = new ArrayList<KalturaSiteCopyJob>();

        rows = sqlService.dbRead(SELECT_BATCH_JOBS_SQL, fields, READER);
        if (rows == null) {
            log.debug("Checked Kaltura Site Copy Job table. Found no rows.");
        } else {
            log.debug("Checked Kaltura Site Copy Job table. Found row for jobId: " + batchId.toString());
        }
        if ((rows != null) && (rows.size() > 0)){
            for(int i =0; i < rows.size(); i++){
                KalturaSiteCopyJob job = (KalturaSiteCopyJob)rows.get(i);
                jobs.add(job);
            }
        }
        return jobs;
    }
    
    /**
    * Add/update a new kaltura site copy job object
    * 
    * @param kalturaSiteCopyJob the {@link KalturaSiteCopyJob} object to add
    * @return true, if added/updated successfully
    */
    public Long save(KalturaSiteCopyJob job , boolean update ){
    
        if (!job.isValid()) {
            log.error("Job details are not valid. Could not create kaltura site copy job:" + job.toString());
        }
        Object fields[] = null;

        if(update){
            fields = new Object[5];
        }else{
            fields = new Object[4];
        }
        fields[0] = job.getBatchId();
        fields[1] = job.getKalturaJobId();
        fields[2] = job.getStatus();
        fields[3] = job.getAttempts();
        Long jobId = null;
        if(update){
            fields[4] = job.getJobId();
            // update existing job record
            sqlService.dbWrite(UPDATE_JOB_SQL, fields);
            jobId = job.getJobId();

        }else{
            jobId = sqlService.dbInsert(null,NEW_JOB_SQL, fields,"JOB_ID");
        }
        if (jobId==null) {
            log.error("Could not create kaltura site copy job");
        } else {
            log.info("Created new kaltura site copy job row:" + jobId);
        }

        return jobId;
    }

    protected static class KalturaSiteCopyJobReader implements SqlReader {
        public Object readSqlResultRecord(ResultSet result) {
            KalturaSiteCopyJob job = new KalturaSiteCopyJob();
            try {
                job.setJobId(result.getLong("JOB_ID"));
                job.setBatchId(result.getLong("BATCH_ID"));
                job.setStatus(result.getString("STATUS"));
                job.setKalturaJobId(result.getLong("KALTURA_JOB_ID"));
                job.setAttempts(result.getInt("ATTEMPTS"));
                job.setDateCreated(result.getDate("CREATED_ON"));
            } catch(SQLException se) {
                log.error("Error parsing Kaltura Site Copy Batch row.",se);
                job = null;
            }
            return job;
        }
    }

    public void setSqlService(SqlService service)
    {
        sqlService = service;
    }

    public SqlService getSqlService()
    {
        return sqlService;
    }

}
