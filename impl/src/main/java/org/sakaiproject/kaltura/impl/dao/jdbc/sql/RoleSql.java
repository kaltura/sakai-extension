/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao.jdbc.sql;

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
