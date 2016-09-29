/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao.jdbc.sql;

/**
 * Supplies the SQL strings for site copy service queries
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class SiteCopyJobSql {

    public static String getFindJobsSql() {
        String sql = "SELECT " +
                        "job_id, " +
                        "batch_id, " +
                        "kaltura_job_id, " +
                        "status, " +
                        "attempts, " +
                        "created_on " +
                     "FROM " +
                        "KALTURA_SITE_COPY_JOB " + 
                     "WHERE " +
                        "status = ? " +
                     "ORDER BY " +
                        "attempts ASC, " +
                        "job_id ASC";

        return sql;
    }

    public static String getJobDetailsSql() {
        String sql = "SELECT " +
                        "job_id, " +
                        "kaltura_job_id, " +
                        "batch_id, " +
                        "status, " +
                        "attempts, " +
                        "created_on " +
                     "FROM " +
                        "KALTURA_SITE_COPY_JOB " +
                     "WHERE " +
                        "job_id = ?";

        return sql;
    }

    public static String getFindBatchJobsSql() {
        String sql = "SELECT " +
                        "job_id, " +
                        "kaltura_job_id, " +
                        "batch_id, " +
                        "status, " +
                        "attempts, " +
                        "created_on " +
                     "FROM " +
                        "KALTURA_SITE_COPY_JOB " +
                     "WHERE " +
                        "batch_id = ?";

        return sql;
    }

    public static String getNewJobSql() {
        String sql = "INSERT INTO " +
                        "KALTURA_SITE_COPY_JOB " +
                     "(" +
                        "batch_id, " +
                        "kaltura_job_id, " +
                        "status, " +
                        "attempts, " +
                        "created_on " +
                     ") " +
                     "VALUES (?, ?, ?, ?, NOW())";

        return sql;
    }

    public static String getUpdateJobSql() {
        String sql = "UPDATE " +
                        "KALTURA_SITE_COPY_JOB " +
                     "SET " +
                        "batch_id = ?, " +
                        "kaltura_job_id = ?, " +
                        "status = ?, " +
                        "attempts = ? " +
                     "WHERE " +
                        "job_id = ?";

        return sql;
    }
}
