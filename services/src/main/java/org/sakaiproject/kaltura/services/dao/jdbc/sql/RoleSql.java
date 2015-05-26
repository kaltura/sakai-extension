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
package org.sakaiproject.kaltura.services.dao.jdbc.sql;

/**
 * Supplies the SQL strings for role service queries
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class RoleSql {


    /**
     * SQL statement to retrieve all roles
     * 
     * @return the SQL string
     */
    public static String getSakaiRoles() {
        String sql = "SELECT " +
                        "ROLE_NAME " +
                     "FROM " +
                        "SAKAI_REALM_ROLE " +
                     "WHERE " +
                        "ROLE_NAME NOT IN ('.anon', '.auth') " +
                     "ORDER BY " +
                        "ROLE_NAME ASC";

        return sql;
    }

}
