package org.amet.services.database;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;

public class DataBaseManager {

    private static DataBaseManager instance;
    private Connection conn;

    private String url;
    private String port;
    private String name;
    private String connectionUrl;
    private String driver;
    private boolean initDataBase;

    private PreparedStatement preparedStatement;

    private DataBaseManager(){
        initConfig();
    }

    public static DataBaseManager getInstance(){
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }


    public void initConfig() {
        String propertiesFile = ClassLoader.getSystemResource("config.properties").getFile();
        Properties prop = new Properties();

        try{
            prop.load(new FileInputStream(propertiesFile));

            url = prop.getProperty("database.url");
            port = prop.getProperty("database.port");
            name = prop.getProperty("database.name");
            connectionUrl = prop.getProperty("database.connectionUrl");
            driver = prop.getProperty("database.driver");
            //initDataBase = prop.getProperty("database.initDatabase");

        }catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void openConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            return;
        }
        conn = DriverManager.getConnection(connectionUrl);
    }

    public void closeConnection() {
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ResultSet executeQuery(String querySQL, Object... params) throws SQLException {
        this.openConnection();
        preparedStatement = conn.prepareStatement(querySQL);
        // Vamos a pasarle los parametros usando preparedStatement
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeQuery();
    }

    public Optional<ResultSet> select(String querySQL, Object... params) throws SQLException {
        return Optional.of(executeQuery(querySQL, params));
    }

    public Optional<ResultSet> select(String querySQL, int limit, int offset, Object... params) throws SQLException {
        String query = querySQL + " LIMIT " + limit + " OFFSET " + offset;
        return Optional.of(executeQuery(query, params));
    }

    public void insert(String insertSQL, Object... params) throws SQLException {

        preparedStatement = conn.prepareStatement(insertSQL, preparedStatement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        preparedStatement.executeUpdate();
//        return Optional.of(preparedStatement.getGeneratedKeys());
    }

    public int update(String updateSQL, Object... params) throws SQLException {
        return updateQuery(updateSQL, params);
    }

    public int delete(String deleteSQL, Object... params) throws SQLException {
        return updateQuery(deleteSQL, params);
    }

    private int updateQuery(String genericSQL, Object... params) throws SQLException {
        preparedStatement = conn.prepareStatement(genericSQL);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeUpdate();
    }

    public void initData(String sqlFile, boolean logWriter) throws FileNotFoundException, SQLException {
        this.openConnection();
        var sr = new ScriptRunner(conn);
        var reader = new BufferedReader(new FileReader(sqlFile));
        if (logWriter) {
            sr.setLogWriter(new PrintWriter(System.out));
        } else {
            sr.setLogWriter(null);
        }
        sr.runScript(reader);
    }
}
