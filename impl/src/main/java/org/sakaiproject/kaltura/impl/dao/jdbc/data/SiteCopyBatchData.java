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
