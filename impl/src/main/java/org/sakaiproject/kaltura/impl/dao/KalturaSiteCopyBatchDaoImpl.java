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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;

/**
 * Implementation of DAO Interface for Kaltura Site Copy Batch 
 * 
 * @author Esh Nagappan (ynagappan @ unicon.net)
 */
public class KalturaSiteCopyBatchDaoImpl implements KalturaSiteCopyBatchDao {

    private static final Log log = LogFactory.getLog(KalturaSiteCopyBatchDaoImpl.class);

    protected SqlService sqlService = null;
   
    protected static final KalturaSiteCopyBatchReader READER = new KalturaSiteCopyBatchReader();

    protected static final String FIND_NEW_JOBS_SQL = "SELECT batch_id,source_site_id,target_site_id,status,attempts,created_on FROM kaltura_site_copy_batch_details WHERE status=? ORDER BY attempts,batch_id";
    protected static final String SELECT_SQL = "SELECT batch_id,source_site_id,target_site_id,status,attempts,created_on FROM kaltura_site_copy_batch_details WHERE batch_id=?";

    private static final String NEW_BATCH_SQL = "INSERT INTO kaltura_site_copy_batch_details(source_site_id, target_site_id, status, attempts, created_on) VALUES (?, ?, ?, ?, NOW())";

    protected static final String UPDATE_BATCH_SQL = "UPDATE kaltura_site_copy_batch_details SET source_site_id=?, target_site_id=?, status=?, attempts=? WHERE batch_id=?";

    protected static final String UPDATE_JOB_STATUS_ONLY_SQL = "UPDATE kaltura_site_copy_batch_details SET status=?, modifiedon=NOW() WHERE batch_id=?";

    /**
     * Check the work queue for any Kaltura Site Copy Batch job with new status 
     * 
     * @return {@link KalturaSiteCopyBatch) object
     */
    public KalturaSiteCopyBatch checkWorkQueue(String status){
        Object fields[] = null;
        fields = new Object[1];
        fields[0] = status;
        List<?> rows = null;
        rows = sqlService.dbRead(FIND_NEW_JOBS_SQL, fields, READER);
        if (rows == null) {
            log.debug("Checked Kaltura Site Copy Batch work queue. Found no rows.");
        } else {
            log.debug("Checked Kaltura Site Copy Batch work queue. Found rows: " + rows.size());
        }

        if ((rows != null) && (rows.size() > 0)){
            KalturaSiteCopyBatch job = (KalturaSiteCopyBatch)rows.get(0);
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
        
        if (batchId == null) {
            throw new IllegalArgumentException("Batch ID cannot be blank.");
        }
        Object fields[] = null;
        fields = new Object[1];
        fields[0] = batchId;
        List<?> rows = null;
        rows = sqlService.dbRead(SELECT_SQL, fields, READER);
        if (rows == null) {
            log.debug("Checked Kaltura Site Copy Batch table. Found no rows.");
        } else {
            log.debug("Checked Kaltura Site Copy Batch table. Found row for batchId: " + batchId.toString());
        }
        if ((rows != null) && (rows.size() > 0)){
            KalturaSiteCopyBatch job = (KalturaSiteCopyBatch)rows.get(0);
            return job;
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
        Object fields[] = null;

        if(update){
            fields = new Object[5];
        }else{
            fields = new Object[4];
        }

        fields[0] = batch.getSourceSiteId();
        fields[1] = batch.getTargetSiteId();
        fields[2] = batch.getStatus();
        fields[3] = batch.getAttempts();
        Long batchId = null;
        if(update){
            fields[4] = batch.getBatchId();
            // update existing batch record
             sqlService.dbWrite(UPDATE_BATCH_SQL, fields);
             batchId = batch.getBatchId();
        }else{
            // add new batch record
            batchId = sqlService.dbInsert(null,NEW_BATCH_SQL, fields,"BATCH_ID");
        }
        if (batchId==null) {
            log.error("Could not create kaltura site copy batch:");
        } else {
            log.info("Created new kaltura site copy batch row:" + batchId);
        }
        return batchId;
    }

    protected static class KalturaSiteCopyBatchReader implements SqlReader {
        public Object readSqlResultRecord(ResultSet result) {
            KalturaSiteCopyBatch batch = new KalturaSiteCopyBatch();
            try {
                batch.setBatchId(result.getLong("BATCH_ID"));
                batch.setStatus(result.getString("STATUS"));
                batch.setSourceSiteId(result.getString("SOURCE_SITE_ID"));
                batch.setTargetSiteId(result.getString("TARGET_SITE_ID"));
                batch.setAttempts(result.getInt("ATTEMPTS"));
            } catch(SQLException se) {
                log.error("Error parsing Kaltura Site Copy Batch row.",se);
                batch = null;
            }
            return batch;
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
