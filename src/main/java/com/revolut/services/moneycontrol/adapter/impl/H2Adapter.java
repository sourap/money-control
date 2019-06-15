package com.revolut.services.moneycontrol.adapter.impl;

import com.revolut.services.moneycontrol.adapter.DBAdapter;
import com.revolut.services.moneycontrol.model.Constants;
import com.revolut.services.moneycontrol.util.Utils;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * H2 DB implementation
 */
public class H2Adapter implements DBAdapter {

    private static final Logger LOGGER = Logger.getLogger(H2Adapter.class.getName());

    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(Constants.H2_JDBC_DRIVER);
        LOGGER.log(Level.INFO, "Connecting to H2 database...");
        return DriverManager.getConnection(Constants.H2_DB_URL, Constants.H2_USER, Constants.H2_PASS);
    }

    @Override
    public void setupTable(String file) throws SQLException, ClassNotFoundException, IOException {
        LOGGER.log(Level.FINE, file + " executing on H2 DB.");
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             Stream<String> stringStream = Utils.readFile(file)) {
            stringStream.forEach(str -> {
                try {
                    statement.executeUpdate(str);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Exception encountered setting up table : ", e);
                }
            });
            LOGGER.log(Level.INFO, file + " executed on H2 DB.");
        }
    }

    @Override
    public List<Map<String, Object>> query(String query) throws SQLException, ClassNotFoundException {
        LOGGER.log(Level.FINE, " executing  query : " + query + " on H2 DB.");
        if (Objects.nonNull(query) && query.length() > 0) {
            try (final Connection connection = getConnection();
                 final Statement statement = connection.createStatement();
                 final ResultSet resultSet = statement.executeQuery(query)) {
                List<Map<String, Object>> list = new ArrayList<>();
                final ResultSetMetaData metaData = resultSet.getMetaData();

                while (resultSet.next()) {
                    Map<String, Object> map = new HashMap<>();
                    for (int counter = 1; counter <= metaData.getColumnCount(); counter++)
                        map.put(metaData.getColumnLabel(counter), resultSet.getObject(counter));
                    list.add(map);
                }
                LOGGER.log(Level.INFO, "Query executed. Fetched records : " + list.size());
                return list;
            }
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean update(Connection connection, String query) throws SQLException {
        if (Objects.isNull(connection) || (Objects.isNull(query) || query.length() == 0)) {
            LOGGER.log(Level.SEVERE, "connection and query expected");
            throw new SQLException("connection and query expected");
        }
        LOGGER.log(Level.FINE, " executing  update query : " + query + " on H2 DB.");
        try (final Statement statement = connection.createStatement()) {
            final int numRows = statement.executeUpdate(query);
            LOGGER.log(Level.INFO, "Query executed. Updated records : " + numRows);
            return (numRows > 0);
        }
    }
}