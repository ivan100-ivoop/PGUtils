package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;
    private String host, database, username, password, type, prefix;
    private File defaultDBFile;
    private int port;

    public DatabaseManager(PGUtils plugin) {
        defaultDBFile = new File(plugin.getDataFolder(), "pgutils.db");
        prefix = plugin.getConfig().getString("database.prefix", "pgutils_");
        host = plugin.getConfig().getString("database.host", "localhost");
        port = plugin.getConfig().getInt("database.port", 3306);
        database = plugin.getConfig().getString("database.dbname", "pgutils");
        username = plugin.getConfig().getString("database.user", "root");
        password = plugin.getConfig().getString("database.pass", "");
        type = plugin.getConfig().getString("database.type", "mysql");
    }

    public void connect() {
        if (!isConnected()) {
            try {
                if (type.equalsIgnoreCase("mysql")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);

                    if (!databaseExists()) {
                        createDatabase();
                    }

                    connection.setCatalog(database);
                } else {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:" + defaultDBFile.getAbsolutePath());
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean databaseExists() throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getCatalogs()) {
            while (resultSet.next()) {
                if (resultSet.getString(1).equalsIgnoreCase(database)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void createDatabase() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database)) {
            preparedStatement.executeUpdate();
            System.out.println("Database created or already exists: " + database);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() { return connection; }

    public boolean tableExists(String tableName) {
        if (isConnected()) {
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet resultSet = metaData.getTables(null, null, tableName, null);
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean execute(String query, Object... parameters) {
        if (isConnected()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                for (int i = 0; i < parameters.length; i++) {
                    preparedStatement.setObject(i + 1, parameters[i]);
                }

                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String fixName(String portals) {
        return prefix + portals;
    }

    public boolean isMysql(){
        return type.equalsIgnoreCase("mysql");
    }

    public boolean executeInsert(String insertQuery, Object... parameters) {
        if (isConnected()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < parameters.length; i++) {
                    preparedStatement.setObject(i + 1, parameters[i]);
                }

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean emptyTable(String tableName) {
        try {
            String deleteQuery = "DELETE FROM " + tableName;
            this.execute(deleteQuery);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<Object[]> executeQuery(String query, Object... parameters) {
        List<Object[]> results = new ArrayList<>();

        if (isConnected()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                for (int i = 0; i < parameters.length; i++) {
                    preparedStatement.setObject(i + 1, parameters[i]);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                            row[i] = resultSet.getObject(i + 1);
                        }
                        results.add(row);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return results;
    }



}
