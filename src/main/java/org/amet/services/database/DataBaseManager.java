package org.amet.services.database;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;

/**
 * Clase que gestiona la conexión con la base de datos
 * y las operaciones CRUD
 * @author Daniel y Diego
 * @version 1.0
 */
public class DataBaseManager {

    /**
     * Instancia de la clase
     * @see DataBaseManager#getInstance()
     */
    private static DataBaseManager instance;

    /**
     * Conexión con la base de datos
     * @see Connection
     */
    private Connection conn;

    /**
     * URL de la base de datos
     * @see String
     */
    private String url;

    /**
     * Puerto de la base de datos
     * @see String
     */
    private String port;

    /**
     * Nombre de la base de datos
     * @see String
     */
    private String name;

    /**
     * URL de conexión con la base de datos
     * @see String
     */
    private String connectionUrl;

    /**
     * Driver de la base de datos
     * @see String
     */
    private String driver;

    /**
     * Indica si se inicializa la base de datos
     * @see boolean
     */
    private boolean initDataBase;

    /**
     * Sentencia preparada
     * @see PreparedStatement
     */
    private PreparedStatement preparedStatement;

    /**
     * Constructor de la clase
     */
    private DataBaseManager(){
        initConfig();
    }

    /**
     * Método que devuelve la instancia de la clase
     * @return DataBaseManager  Instancia de la clase
     */
    public static DataBaseManager getInstance(){
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }


    /**
     * Método que inicializa la configuración de la base de datos
     * @see Properties
     * @see FileInputStream
     * @see IOException
     * @see FileNotFoundException
     * @see RuntimeException
     */
    public void initConfig() {
        InputStream propertiesFile = ClassLoader.getSystemResourceAsStream("config.properties");
        Properties prop = new Properties();

        try{
            prop.load(propertiesFile);

            url = prop.getProperty("database.url");
            port = prop.getProperty("database.port");
            name = prop.getProperty("database.name");
            connectionUrl = prop.getProperty("database.connectionUrl");
            driver = prop.getProperty("database.driver");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Método que abre la conexion a la base de datos
     * @see SQLException
     * @throws SQLException Error al abrir la conexion
     */
    public void openConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            return;
        }
        conn = DriverManager.getConnection(connectionUrl);
    }

    /**
     * Método que cierra la conexion a la base de datos
     * @see SQLException
     */
    public void closeConnection() {
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Método que ejecuta una consulta a la base de datos
     * @see ResultSet
     * @see SQLException
     * @param querySQL Consulta a la base de datos
     * @param params Parametros de la consulta
     * @return ResultSet Resultado de la consulta
     */
    private ResultSet executeQuery(String querySQL, Object... params) throws SQLException {
        this.openConnection();
        preparedStatement = conn.prepareStatement(querySQL);
        // Vamos a pasarle los parametros usando preparedStatement
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeQuery();
    }

    /**
     * Método que ejecuta una consulta select a la base de datos
     * @see ResultSet
     * @see SQLException
     * @param querySQL Consulta a la base de datos
     * @param params Parametros de la consulta
     * @return ResultSet Resultado de la consulta
     * @throws SQLException Error al ejecutar la consulta select
     */
    public Optional<ResultSet> select(String querySQL, Object... params) throws SQLException {
        return Optional.of(executeQuery(querySQL, params));
    }

    /**
     * Método que ejecuta una consulta insert a la base de datos
     * @see ResultSet
     * @see SQLException
     * @param insertSQL Consulta a la base de datos
     * @param params Parametros de la consulta
     * @return ResultSet Resultado de la consulta
     * @throws SQLException Error al ejecutar la consulta insert
     */
    public void insert(String insertSQL, Object... params) throws SQLException {

        preparedStatement = conn.prepareStatement(insertSQL, preparedStatement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        preparedStatement.executeUpdate();
    }


    /**
     * Metodo que ejecuta un archivo sql para inicializar la base de datos
     * @see ScriptRunner
     * @see BufferedReader
     * @see FileReader
     * @see PrintWriter
     * @see SQLException
     * @see FileNotFoundException
     * @param sqlFile path archivo sql
     * @param logWriter Indica si se escribe el log
     * @throws FileNotFoundException Error al encontrar el archivo sql
     * @throws SQLException Error al ejecutar el archivo sql
     */
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
