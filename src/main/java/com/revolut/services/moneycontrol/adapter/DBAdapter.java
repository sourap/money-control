package com.revolut.services.moneycontrol.adapter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DB Adapter to manage communication with a given DB
 */
public interface DBAdapter {

    /**
     * Get Connection Object for the given DB implementation
     *
     * @return Connection Object
     * @throws ClassNotFoundException Driver class not found
     * @throws SQLException SQLException
     */
    Connection getConnection() throws ClassNotFoundException, SQLException;

    /**
     * Setup Tables with the scripts provided
     *
     * @param file sql script files
     * @throws SQLException SQLException
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws IOException IOException
     */
    void setupTable(String file) throws SQLException, ClassNotFoundException, IOException;

    /**
     * Queries and retrieves back the result
     *
     * @param query QueryString
     * @return List of Key Value pairs
     * @throws SQLException SQLException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    List<Map<String, Object>> query(String query) throws SQLException, ClassNotFoundException;

    /**
     * Update DB record with the query provided
     *
     * @param connection Connection Object
     * @param query QueryString
     * @return Update Status
     * @throws SQLException SQLException
     */
    boolean update(Connection connection, String query) throws SQLException;
}
