/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao.jdbc.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.kaltura.impl.dao.jdbc.sql.RoleSql;
import org.sakaiproject.kaltura.impl.dao.jdbc.Database;

/**
 * Processes app-specific role services dao needs
 * 
 * @author Robert Long (rlong @ unicon.net)
 *
 * This should be removed in the future once the sakai api implements this
 */
@Deprecated
public class RoleData extends Database {

    private static final Logger log = LoggerFactory.getLogger(RoleData.class);

    /**
     * Gets all Sakai roles defined from the database
     * 
     * @return a list of the Sakai role IDs
     */
    public List<String> getSakaiRoles() {
        List<String> sakaiRolesList = new ArrayList<String>();

        PreparedStatement preparedStatement = null;

        try {
            String query = RoleSql.getSakaiRoles();

            preparedStatement = createPreparedStatement(preparedStatement, query);

            ResultSet resultSet = executeQueryPreparedStatement(preparedStatement);

            while(resultSet.next()) {
                sakaiRolesList.add(resultSet.getString("ROLE_NAME"));
            }
        } catch (Exception e) {
            log.error("Error getting Sakai roles data. Error: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return sakaiRolesList;
    }

}
