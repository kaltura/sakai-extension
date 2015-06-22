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
import org.sakaiproject.kaltura.impl.dao.jdbc.sql.SiteCopyJobSql;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;

public class SiteCopyJobData  extends Database {

    private final Log log = LogFactory.getLog(SiteCopyJobData.class);

    public List<KalturaSiteCopyJob> getJobs(String status) {
        List<KalturaSiteCopyJob> jobs = new ArrayList<KalturaSiteCopyJob>();

        PreparedStatement preparedStatement = null;

        try {
            String query = SiteCopyJobSql.getFindJobsSql();
            preparedStatement = createPreparedStatement(preparedStatement, query);

            preparedStatement.setString(1, status);

            ResultSet resultSet = executeQueryPreparedStatement(preparedStatement);

            while(resultSet.next()) {
                KalturaSiteCopyJob kalturaSiteCopyJob = new KalturaSiteCopyJob(
                    resultSet.getLong("job_id"),
                    resultSet.getLong("batch_id"),
                    resultSet.getLong("kaltura_job_id"),
                    resultSet.getString("status"),
                    resultSet.getInt("attempts"),
                    resultSet.getDate("created_on"));

                jobs.add(kalturaSiteCopyJob);
            }
        } catch (Exception e) {
            log.error("Error getting site copy job status data. Error: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return jobs;
    }

    public KalturaSiteCopyJob getJob(Long jobId) {
        KalturaSiteCopyJob kalturaSiteCopyJob = null;

        PreparedStatement preparedStatement = null;

        try {
            String query = SiteCopyJobSql.getJobDetailsSql();
            preparedStatement = createPreparedStatement(preparedStatement, query);

            preparedStatement.setLong(1, jobId);

            ResultSet resultSet = executeQueryPreparedStatement(preparedStatement);

            while(resultSet.next()) {
                kalturaSiteCopyJob = new KalturaSiteCopyJob(
                        resultSet.getLong("job_id"),
                        resultSet.getLong("batch_id"),
                        resultSet.getLong("kaltura_job_id"),
                        resultSet.getString("status"),
                        resultSet.getInt("attempts"),
                        resultSet.getDate("created_on"));
            }
        } catch (Exception e) {
            log.error("Error getting site copy job data. Error: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return kalturaSiteCopyJob;
    }

    public List<KalturaSiteCopyJob> getBatchJobs(Long batchId) {
        List<KalturaSiteCopyJob> jobs = new ArrayList<KalturaSiteCopyJob>();

        PreparedStatement preparedStatement = null;

        try {
            String query = SiteCopyJobSql.getFindJobsSql();
            preparedStatement = createPreparedStatement(preparedStatement, query);

            preparedStatement.setLong(1, batchId);

            ResultSet resultSet = executeQueryPreparedStatement(preparedStatement);

            while(resultSet.next()) {
                KalturaSiteCopyJob kalturaSiteCopyJob = new KalturaSiteCopyJob(
                    resultSet.getLong("job_id"),
                    resultSet.getLong("batch_id"),
                    resultSet.getLong("kaltura_job_id"),
                    resultSet.getString("status"),
                    resultSet.getInt("attempts"),
                    resultSet.getDate("created_on"));

                jobs.add(kalturaSiteCopyJob);
            }
        } catch (Exception e) {
            log.error("Error getting site copy job status data. Error: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return jobs;
    }

    public Long persistJob(KalturaSiteCopyJob job, boolean update) {
        Long jobId = null;

        PreparedStatement preparedStatement = null;

        try {
            String query = (update) ? SiteCopyJobSql.getUpdateJobSql() : SiteCopyJobSql.getNewJobSql();
            preparedStatement = createPreparedStatement(preparedStatement, query);

            preparedStatement.setLong(1, job.getBatchId());
            preparedStatement.setLong(2, job.getKalturaJobId());
            preparedStatement.setString(3, job.getStatus());
            preparedStatement.setInt(4, job.getAttempts());

            if (update) {
                preparedStatement.setLong(5, job.getJobId());
            }
            jobId = executeUpdatePreparedStatement(preparedStatement);
        } catch (Exception e) {
            log.error("Error persisting site copy job data. Error: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return jobId;
    }

}
