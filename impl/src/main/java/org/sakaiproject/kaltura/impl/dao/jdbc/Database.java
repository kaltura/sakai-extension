/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.db.api.SqlService;

/**
 * General database operations
 * Uses the SqlService class from Sakai
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 * This should be removed in the future once RoleData can be removed
 */
@Deprecated
public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private SqlService sqlService;
    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    /**
     * Borrows a connection from the pool
     * 
     * @return the connection object
     */
    private Connection borrowConnection() {
        Connection connection = null;

        try {
            connection = sqlService.borrowConnection();
        } catch (Exception e) {
            log.error("Cannot get database connection: " + e, e);
        }

        return connection;
    }

    /**
     * Returns the borrowed connection to the pool
     * 
     * @param connection the connection object
     */
    private void returnConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                sqlService.returnConnection(connection);
            }
        } catch (Exception e) {
            log.error("Error returning connection to pool: " + e, e);
        }
    }

    /**
     * Prepares the prepared statement with the SQL provided
     * 
     * @param preparedStatement the prepared statement
     * @param sql the SQL string
     * @return the prepared statement
     */
    protected PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String sql) {
        Connection connection = borrowConnection();

        try {
            preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        } catch (Exception e) {
            log.error("Error creating prepared statement: " + e, e);
        }

        return preparedStatement;
    }

    /**
     * Closes a prepared statement, after returning the borrowed connection
     * 
     * @param preparedStatement the prepared statement
     */
    protected void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                try {
                    returnConnection(preparedStatement.getConnection());
                } catch (Exception e) {
                    log.error("Error returning connection to pool: " + e, e);
                } finally {
                    preparedStatement.close();
                }
            }
        } catch (Exception e) {
            log.error("Error closing prepared statement: " + e, e);
        }
    }

    /**
     * Executes the given prepared statement (select)
     * 
     * @param preparedStatement the prepared statement
     * @return the ResultSet from the query
     */
    protected ResultSet executeQueryPreparedStatement(PreparedStatement preparedStatement) {
        ResultSet resultSet = null;

        try {
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            log.error("Cannot perform database call: " + e, e);
        }

        return resultSet;
    }

    /**
     * Executes the given prepared statement (update, insert, delete)
     * 
     * @param preparedStatement the prepared statement
     * @return the key from execution
     */
    protected Long executeUpdatePreparedStatement(PreparedStatement preparedStatement) {
        Long key = null;

        try {
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                key = resultSet.getLong(1);
            }
        } catch (Exception e) {
            log.error("Cannot perform database call: " + e, e);
        }

        return key;
    }

}
