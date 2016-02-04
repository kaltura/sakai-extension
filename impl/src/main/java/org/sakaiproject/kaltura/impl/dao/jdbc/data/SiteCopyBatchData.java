/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao.jdbc.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kaltura.impl.dao.jdbc.Database;
import org.sakaiproject.kaltura.impl.dao.jdbc.sql.SiteCopyBatchSql;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;

public class SiteCopyBatchData  extends Database {

    private final Log log = LogFactory.getLog(SiteCopyBatchData.class);

    public List<KalturaSiteCopyBatch> getJobs(String status) {
        List<KalturaSiteCopyBatch> jobs = new ArrayList<KalturaSiteCopyBatch>();

        PreparedStatement preparedStatement = null;

        try {
            String query = SiteCopyBatchSql.getFindJobsSql();
            preparedStatement = createPreparedStatement(preparedStatement, query);

            preparedStatement.setString(1, status);

            ResultSet resultSet = executeQueryPreparedStatement(preparedStatement);

            while(resultSet.next()) {
                KalturaSiteCopyBatch kalturaSiteCopyBatch = new KalturaSiteCopyBatch(
                    resultSet.getLong("batch_id"),
                    resultSet.getString("source_site_id"),
                    resultSet.getString("target_site_id"),
                    resultSet.getString("status"),
                    resultSet.getInt("attempts"),
                    resultSet.getDate("created_on"));

                jobs.add(kalturaSiteCopyBatch);
            }
        } catch (Exception e) {
            log.error("Error getting site copy status data. Error: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return jobs;
    }

    public KalturaSiteCopyBatch getJob(Long batchId) {
        KalturaSiteCopyBatch kalturaSiteCopyBatch = null;

        PreparedStatement preparedStatement = null;

        try {
            String query = SiteCopyBatchSql.getBatchDetailsSql();
            preparedStatement = createPreparedStatement(preparedStatement, query);

            preparedStatement.setLong(1, batchId);

            ResultSet resultSet = executeQueryPreparedStatement(preparedStatement);

            while(resultSet.next()) {
                kalturaSiteCopyBatch = new KalturaSiteCopyBatch(
                    resultSet.getLong("batch_id"),
                    resultSet.getString("source_site_id"),
                    resultSet.getString("target_site_id"),
                    resultSet.getString("status"),
                    resultSet.getInt("attempts"),
                    resultSet.getDate("created_on"));
            }
        } catch (Exception e) {
            log.error("Error getting site copy batch data. Error: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return kalturaSiteCopyBatch;
    }

    public Long persistBatch(KalturaSiteCopyBatch batch, boolean update) {
        Long batchId = null;

        PreparedStatement preparedStatement = null;

        try {
            String query = (update) ? SiteCopyBatchSql.getUpdateBatchSql() : SiteCopyBatchSql.getNewBatchSql();
            preparedStatement = createPreparedStatement(preparedStatement, query);

            preparedStatement.setString(1, batch.getSourceSiteId());
            preparedStatement.setString(2, batch.getTargetSiteId());
            preparedStatement.setString(3, batch.getStatus());
            preparedStatement.setInt(4, batch.getAttempts());
            if(update){
                preparedStatement.setLong(5,batch.getBatchId());
            }
            batchId = executeUpdatePreparedStatement(preparedStatement);
        } catch (Exception e) {
            log.error("Error persisting site copy batch data. Error: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        if(update){
            batchId = batch.getBatchId();
        }
        return batchId;
    }

}
