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
package org.sakaiproject.kaltura.impl.dao.jdbc.sql;

/**
 * Supplies the SQL strings for site copy service queries
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class SiteCopyBatchSql {

    public static String getFindJobsSql() {
        String sql = "SELECT " +
                        "batch_id, " +
                        "source_site_id, " +
                        "target_site_id, " +
                        "status, " +
                        "attempts, " +
                        "created_on " +
                     "FROM " +
                        "kaltura_site_copy_batch " +
                     "WHERE " +
                        "status = ? " +
                     "ORDER BY " +
                        "attempts ASC," +
                        "batch_id ASC";

        return sql;
    }

    public static String getBatchDetailsSql() {
        String sql = "SELECT " +
                        "batch_id, " +
                        "source_site_id, " +
                        "target_site_id, " +
                        "status, " +
                        "attempts, " +
                        "created_on " +
                     "FROM " +
                        "kaltura_site_copy_batch " +
                     "WHERE " +
                        "batch_id = ?";

        return sql;
    }

    public static String getNewBatchSql() {
        String sql = "INSERT INTO " +
                        "kaltura_site_copy_batch " +
                        "(" +
                            "source_site_id, " +
                            "target_site_id, " +
                            "status, " +
                            "attempts, " +
                            "created_on" +
                        ") " +
                        "VALUES (?, ?, ?, ?, NOW())";

        return sql;
    }

    public static String getUpdateBatchSql() {
        String sql = "UPDATE " +
                        "kaltura_site_copy_batch " +
                     "SET " +
                        "source_site_id = ?, " +
                        "target_site_id = ?, " +
                        "status = ?, " +
                        "attempts = ? " +
                     "WHERE " +
                        "batch_id = ?";

        return sql;
    }
}
