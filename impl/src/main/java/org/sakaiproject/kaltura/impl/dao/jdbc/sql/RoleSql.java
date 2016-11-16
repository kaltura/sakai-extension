/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao.jdbc.sql;

/**
 * Supplies the SQL strings for role service queries
 * 
 * @author Robert Long (rlong @ unicon.net)
 *
 * This should be removed in the future once RoleData can be removed
 */
@Deprecated
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
                        "ROLE_NAME NOT LIKE ('.%') " +
                     "ORDER BY " +
                        "ROLE_NAME ASC";

        return sql;
    }

}
