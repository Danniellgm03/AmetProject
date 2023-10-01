# Amet

### Miembros del equipo

[Daniel Garrido Muros](https://github.com/Danniellgm03)

[Diego Torres Mijarra](https://github.com/DiegoTorresMijarra)

## Modelos

### Clase pojo Amet

Immplementación clase pojo Amet con lombok

``` java
@Data
@Builder
@Getter
@Setter
public class Amet {

    /**
     * Localidad de la medicion
     * @see String
     */
    private String localidad;

    /**
     * Provincia de la medicion
     * @see String
     */
    private String provincia;

    /**
     * Temperatura maxima de la medicion
     * @see double
     */
    private double temp_max;

    /**
     * Hora de la temperatura maxima de la medicion
     * @see LocalTime
     */
    private LocalTime hour_temp_max;

    /**
     * Temperatura minima de la medicion
     * @see double
     */
    private double temp_min;

    /**
     * Hora de la temperatura minima de la medicion
     * @see LocalTime
     */
    private LocalTime hour_temp_min;

    /**
     * Precipitacion de la medicion
     * @see double
     */
    private double precipitacion;

    /**
     * Dia de la medicion
     * @see LocalDate
     */
    private LocalDate dia;

    /**
     * To string
     * @return String
     */
    @Override
    public String toString() {
        return "Amet{" +
                "localidad='" + localidad + '\'' +
                ", provincia='" + provincia + '\'' +
                ", temp_max=" + temp_max +
                ", hour_temp_max=" + hour_temp_max +
                ", temp_min=" + temp_min +
                ", hour_temp_min=" + hour_temp_min +
                ", precipitacion=" + precipitacion +
                ", dia=" + dia +
                '}';
    }
}
```

### UTILS

En el directorio utils se encontraran las utilidades necesarias para convertir a json, adaptadores para gson ...


#### files

En files encontraremos la clase para exportar los datos de una provincia obtenida a traves del csv a un json utilizando GSON

``` java 
public class JsonFileAmet {

    /**
     * Exporta los datos de las mediciones a un fichero JSON en la carpeta
     * json dentro de data del proyecto con el nombre de la pronvincia
     * @param provincia Provincia
     * @param mediciones Lista de mediciones
     * @param path Ruta del fichero
     * @throws Exception Excepcion
     */
    public static void exportJsonAmetProvincia(String provincia, List<Amet> mediciones, String path) throws Exception {

        path += File.separator + "json" + File.separator + provincia + ".json";
        var provincia_data = mediciones.stream()
                .filter((x) -> x.getProvincia().equals(provincia)).toList();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .setPrettyPrinting().create();
        String json = gson.toJson(provincia_data);
        Files.write(Path.of(path), json.getBytes(StandardCharsets.ISO_8859_1));
    }

}
```


#### Locale

En el paquete locale encontraremos los adaptadores para la exportación del json, para la traduccion de clases como LocalDate sea capaz de leerlas y escribirlas en el json

Aqui encontraremos dos clases:

LocalDateAdapter:
``` java
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    /**
     * Metodo que escribe un objeto LocalDate en un JsonWriter
     * @param jsonWriter JsonWriter
     * @param localDate LocalDate
     */
    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        jsonWriter.value(localDate.toString());
    }

    /**
     * Metodo que lee un objeto LocalDate de un JsonReader
     * @param jsonReader JsonReader
     * @return LocalDate
     */
    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        return LocalDate.parse(jsonReader.nextString());
    }
}

```

LocalTimeAdapter:
``` java
public class LocalTimeAdapter  extends TypeAdapter<LocalTime> {

    /**
     * Metodo que escribe un objeto LocalDate en un JsonWriter
     * @param jsonWriter JsonWriter
     * @param localTime LocalDate
     */
    @Override
    public void write(com.google.gson.stream.JsonWriter jsonWriter, LocalTime localTime) throws IOException {
        jsonWriter.value(localTime.toString());
    }

    /**
     * Metodo que lee un objeto LocalDate de un JsonReader
     * @param jsonReader JsonReader
     * @return LocalDate
     */
    @Override
    public LocalTime read(com.google.gson.stream.JsonReader jsonReader) throws java.io.IOException {
        return LocalTime.parse(jsonReader.nextString());
    }
}
```

#### Raiz Utils

En la raiz del paquete utils podremos encontrar dos clases.

AmetUtils:
Esta clase nos servira para incorporar metodos que nos sirvan a la hora de tratar los datos u diversas tareas relacionado con la clase amet.

En ella encontraremos un metodo que obtendra el dia en el que se recogieron los datos de las mediciones del csv aportado

```java
public class AmetUtils {

    /**
     * Método que devuelve el día de un fichero de Aemet
     * @param route_file Ruta del fichero
     * @return LocalDate
     */
    public static LocalDate getDayOfNameFile(String route_file){

        String filename = Path.of(route_file).getFileName().toString();
        String diaString = filename.split("Aemet")[1].split("\\.")[0];
        DateTimeFormatter formatter_day = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

        return LocalDate.parse(diaString, formatter_day);
    }
}

```


FileEncoding:

Esta clase nos servira para convertir un archivo en un formato windows a UFT-8 para que se pueda interpretar bien los caracteres en el CSV.

``` java
public class FileEncoding {

    /**
     * Codifica un fichero de entrada a UTF-8, leyendo un archivo y creando otro nuevo con la codificación UTF-8
     * @param inputFile Fichero de entrada
     * @param outputFile Fichero de salida
     * @return Path del fichero de salida codificado
     * @throws IOException  Error de entrada/salida del archivo a codificar
     */
    public static String convertFileToUTF8(String inputFile, String outputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "windows-1252"));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true), StandardCharsets.UTF_8))) {

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        }
        return outputFile;
    }

}
```


### SERVICES

En el paquete services nos encontraremos las diferentes clases que nos permita acceder a los datos, ya se a un csv o una base de datos

#### Amet

En el paquete amet se podra encontrar la clase encargada de leer el fichero csv entero y un metodo para insertar las mediciones a base de datos utilizando la clase controlador.

```java
public class AmetCsvManager {

    /**
     * Lista de mediciones
     * @see Amet
     * @see List
     * @see ArrayList
     */
    private final List<Amet> mediciones = new ArrayList<>();

    /**
     * Lee un archivo csv
     * @param route_file ruta del archivo csv
     * @return List Lista de mediciones
     */
    public List<Amet> readAllCSV(String route_file){
        LocalDate dia = AmetUtils.getDayOfNameFile(route_file);
        try {
            List<String> lines =  Files.newBufferedReader(Paths.get(route_file), StandardCharsets.UTF_8 ).lines().toList();

            for (String line : lines) {
                String[] data = line.split(";");
                data[3] = data[3].length() == 4 ? "0" + data[3] : data[3];
                data[5] = data[5].length() == 4 ? "0" + data[5] : data[5];

                String localidad = data[0];
                String provincia = data[1];
                double temp_max = Double.parseDouble(data[2]);
                LocalTime hour_temp_max = LocalTime.parse(data[3]);
                double temp_min = Double.parseDouble(data[4]);
                LocalTime hour_temp_min = LocalTime.parse(data[5]);
                double precipitacion = Double.parseDouble(data[6]);

                mediciones.add(
                        Amet.builder()
                                .localidad(localidad)
                                .provincia(provincia)
                                .temp_max(temp_max)
                                .hour_temp_max(hour_temp_max)
                                .temp_min(temp_min)
                                .hour_temp_min(hour_temp_min)
                                .precipitacion(precipitacion)
                                .dia(dia)
                                .build()
                );
            }


            return mediciones;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserta todas las mediciones leidas en la base de datos
     * @see AmetController
     * @see AmetRepositoryImpl
     * @see DataBaseManager
     * @see Amet
     */
    public void insertAllMediciones(){
        AmetController controller = new AmetController(new AmetRepositoryImpl(DataBaseManager.getInstance()));
        for(Amet medicion : this.mediciones){
            controller.insertMedicion(medicion);
        }

    }

}

```


### Database

En el paquete database podremos encontrar un manegador de base de datos que utiliza el patron de diseño Singleton que nos permite asegurarnos de que una clase tenga una única instancia

En esta clase haremos uso del archivo de propiedades que se encuentra en el directorio resources y el script para inicializar la base de datos. Ademas de sus correspondientes despendencias.

```java
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
        String propertiesFile = ClassLoader.getSystemResource("config.properties").getFile();
        Properties prop = new Properties();

        try{
            prop.load(new FileInputStream(propertiesFile));

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

```

Entre las dependencias necesarias para poder utilizar la clase podremos encontrar las siguientes:

El driver para nuestra base de datos sqlite y mybatis para poder ejecutar el script 

```kts
implementation("org.xerial:sqlite-jdbc:3.43.0.0")
implementation("org.mybatis:mybatis:3.5.13")
```

## REPOSITORIES

En el paquete repositories podremos encontrar dos paquetes.

El paquete base que tendra una interfaz que sera utilizada para implementar sus metodos en el repositorio de amet y su vez esta interfaz sera extendida por AmetRepository para agregar sus metodos adicionales

```java
public interface CrudRepository<T> {

    /**
     * Encontrar todas las entidades
     * @return List Lista de entidades
     * @throws SQLException Excepcion
     */
    List<T> findAll() throws SQLException;

    /**
     * Encontrar una entidad por su id
     * @param id Id de la entidad
     * @return T Entidad
     * @throws SQLException - Excepcion
     */
    T findById(Integer id) throws SQLException;

    /**
     * Insertar una entidad
     * @param entity Entidad
     * @return T  Entidad insertada
     * @throws SQLException - Excepcion
     */
    T insert(T entity) throws SQLException;

    /**
     * Actualizar una entidad
     * @param entity  Entidad
     * @return T  Entidad actualizada
     * @throws SQLException  Excepcion
     */
    T update(T entity)  throws SQLException ;

    /**
     * Eliminar una entidad
     * @param entity  Entidad
     * @return T  Entidad eliminada
     * @throws SQLException  Excepcion
     */
    T delete(T entity)  throws SQLException ;

}
```

Esta interfaz a su vez sera extendia por AmetRepository, agreando dos metodos:

- findById para poder encontrar una medición por su id
- deleteAll para poder borrar todas las mediciones de la base de datos

``` java
public interface AmetRepository extends CrudRepository<Amet> {

    /**
     * Encontrar medicion por id
     * @return List Lista de Mediciones
     */
    Amet findById(Integer id) throws SQLException;

    /**
     * Borrar todas las mediciones
     * @return List Lista de Mediciones
     */
    List<Amet> deleteAll();

}
```

AmetRepostiroryImpl sera una implementación de la interfaz AmetRepository y se encontrara los metodos implementados

```java
public class AmetRepositoryImpl implements AmetRepository {

    /**
     * Instancia de la base de datos
     * @see DataBaseManager
     */
    private final DataBaseManager dataBaseManager;

    /**
     * Constructor
     * @param database - Instancia de la base de datos
     */
    public AmetRepositoryImpl(DataBaseManager database){
        dataBaseManager = database;
    }

    /**
     * Encontrar todas las mediciones
     * @return List Lista de mediciones
     */
    @Override
    public List<Amet> findAll() throws SQLException {
        dataBaseManager.openConnection();
        String sql = "SELECT * FROM mediciones";
        var result = dataBaseManager.select(sql).orElseThrow(() -> new SQLException("Error al obtener todas las mediciones"));

        List<Amet> mediciones = new ArrayList<>();

        while (result.next()){
            Amet medicion = Amet.builder()
                    .localidad(new String(result.getString("localidad").getBytes(StandardCharsets.UTF_8)))
                    .provincia(new String(result.getString("provincia").getBytes(StandardCharsets.UTF_8)))
                    .temp_max(result.getDouble("temp_max"))
                    .hour_temp_max(LocalTime.parse(result.getString("hour_temp_max")))
                    .temp_min(result.getDouble("temp_min"))
                    .hour_temp_min(LocalTime.parse(result.getString("hour_temp_min")))
                    .precipitacion(result.getDouble("precipitacion"))
                    .dia(LocalDate.parse(result.getString("dia")))
                    .build();

            mediciones.add(medicion);
        }

        dataBaseManager.closeConnection();
        return mediciones;
    }

    /**
     * Encontrar todas las mediciones por provincia
     * @param provincia  Provincia
     * @return List  Lista de mediciones
     * @throws SQLException  Error al obtener las mediciones por provincia
     */
    public List<Amet> findByProvincia(String provincia) throws SQLException{
        dataBaseManager.openConnection();
        String sql = "SELECT * FROM mediciones WHERE provincia = ?";
        var result = dataBaseManager.select(sql, provincia).orElseThrow(() -> new SQLException("Error al obtener la medicion por provincia"));

        List<Amet> mediciones = new ArrayList<>();

        while (result.next()){
            Amet medicion = Amet.builder()
                    .localidad(result.getString("localidad"))
                    .provincia(result.getString("provincia"))
                    .temp_max(result.getDouble("temp_max"))
                    .hour_temp_max(LocalTime.parse(result.getString("hour_temp_max")))
                    .temp_min(result.getDouble("temp_min"))
                    .hour_temp_min(LocalTime.parse(result.getString("hour_temp_min")))
                    .precipitacion(result.getDouble("precipitacion"))
                    .dia(LocalDate.parse(result.getString("dia")))
                    .build();

            mediciones.add(medicion);
        }

        dataBaseManager.closeConnection();
        return mediciones;
    }


    /**
     * Encontrar medicion por ID
     * @param id  Id de medicion
     * @return Amet  Medicion
     */
    @Override
    public Amet findById(Integer id) throws SQLException {
        dataBaseManager.openConnection();
        String sql = "SELECT * FROM mediciones WHERE ID = ?";
        var result = dataBaseManager.select(sql, id).orElseThrow(() -> new SQLException("Error al obtener la medicion por id"));
        result.next();
        Amet medicion = Amet.builder()
                .localidad(result.getString("localidad"))
                .provincia(result.getString("provincia"))
                .temp_max(result.getDouble("temp_max"))
                .hour_temp_max(LocalTime.parse(result.getString("hour_temp_max")))
                .temp_min(result.getDouble("temp_min"))
                .hour_temp_min(LocalTime.parse(result.getString("hour_temp_min")))
                .precipitacion(result.getDouble("precipitacion"))
                .dia(LocalDate.parse(result.getString("dia")))
                .build();

        dataBaseManager.closeConnection();
        return medicion;
    }

    /**
     * Insertar una medicion
     * @param medicion  Medicion
     * @return Amet  Medicion insertada
     */
    @Override
    public Amet insert(Amet medicion) throws SQLException {
        String sql = "INSERT INTO mediciones VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?)";
        dataBaseManager.openConnection();
        dataBaseManager.insert(sql,
                medicion.getLocalidad(),
                medicion.getProvincia(),
                medicion.getTemp_max(),
                medicion.getHour_temp_max().toString(),
                medicion.getTemp_min(),
                medicion.getHour_temp_min().toString(),
                medicion.getPrecipitacion(),
                medicion.getDia().toString()
        );

        dataBaseManager.closeConnection();
        return null;
    }

    /**
     * Actualizar una medicion
     * @param medicion  Medicion
     * @return Amet  Medicion actualizada
     */
    @Override
    public Amet update(Amet medicion) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Eliminar una medicion
     * @param medicion  Medicion
     * @return Amet  Medicion eliminada
     */
    @Override
    public Amet delete(Amet medicion) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Eliminar todas las mediciones
     * @return List  Lista de mediciones eliminadas
     */
    @Override
    public List<Amet> deleteAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
```


## CONTROLLERS

Dentro de este paquete encontraremos la clase encargada de recuperar/solicitar los datos al repositorio para devolverselos al usuario.


```java
public class AmetController {

    /**
     * Repositorio de mediciones
     * @see AmetRepositoryImpl
     */
    private AmetRepositoryImpl ametRepository;

    /**
     * Constructor
     * @param ametRepository - Repositorio de mediciones
     */
    public AmetController(AmetRepositoryImpl ametRepository){
        this.ametRepository = ametRepository;
    }

    /**
     * Encuentra todas las mediciones de la base de datos
     * @return List  Lista de mediciones
     */
    public List<Amet> findAll(){
        try {
            return ametRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserta una medicion en la base de datos
     * @param medicion - Medicion a insertar
     * @return Amet Medicion insertada
     */
    public Amet insertMedicion(Amet medicion){
        Amet inserted = null;
        try {
            inserted = ametRepository.insert(medicion);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return inserted;
    }

    /**
     * Buscar medicion por id
     * @param id  Id de la medicion
     * @return Amet  Medicion encontrada
     */
    public Amet findById(Integer id){
        try {
            return ametRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Buscar mediciones por provincia
     * @param provincia - Provincia de la medicion
     * @return List Lista de mediciones encontradas
     */
    public List<Amet> findByProvincia(String provincia){
        try {
            return ametRepository.findByProvincia(provincia);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
```


## MAIN 

En la clase Main y el punto partida de la aplicación encontraremos las diferentes consultas pedidas en el ejercicio

El codigo para resaltar es el siguiente:

Metodo para obtener una información dada de una provincia en especifica
```java
/**
     * Método que muestra los datos de una provincia
     * @param nombre_provincia Nombre de la provincia
     * @param mediciones Lista de mediciones
     */
    public static void datosByProvincia(String nombre_provincia, List<Amet> mediciones){
        System.out.println("Datos de las provincia de "+nombre_provincia+" (debe funcionar para cualquier provincia)");
        var groupingby_provincia = mediciones.stream()
                .filter((x) -> x.getProvincia().equals(nombre_provincia))
                .collect(Collectors.groupingBy(
                        (x) -> x.getDia().toString(),
                        Collectors.toList()
                ));

        System.out.println();

        var maximo_groupingby = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .max(Comparator.comparingDouble(Amet::getTemp_max))
                ));
        System.out.println(maximo_groupingby);
        System.out.println();


        var minimo_groypinby = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .min(Comparator.comparingDouble(Amet::getTemp_min))
                ));
        System.out.println(minimo_groypinby);
        System.out.println();

        var media_max_temp = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.averagingDouble(Amet::getTemp_max))
                ));
        System.out.println(media_max_temp);
        System.out.println();

        var media_min_temp = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.averagingDouble(Amet::getTemp_min))
                ));
        System.out.println(media_min_temp);
        System.out.println();

        var maximo_groupingby_precipitacion = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .max(Comparator.comparingDouble(Amet::getPrecipitacion))
                ));
        System.out.println(maximo_groupingby_precipitacion);
        System.out.println();

        var media_precipitacion = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.averagingDouble(Amet::getPrecipitacion))
                ));
        System.out.println(media_precipitacion);
        System.out.println();
    }
```


Acceso a ficheros csv con las mediciones proporcionadas
```java
    String[] files = {"UTF8Aemet20171030.csv", "UTF8Aemet20171031.csv", "UTF8Aemet20171029.csv"};
    for (String file: files) {
        String ruta_file = ruta_absoluta + File.separator + file ;
        managerAmet.readAllCSV(ruta_file);
    }
```



## RESOURCES

Como recursos nos encontraremos los siguientes archivos.

#### config.properties

Datos necesarios para una correcta conexión a la base de datos
```properties
database.url=jdbc:sqlite
database.port=3306
database.name=amet
database.connectionUrl=jdbc:sqlite:amet.db
database.driver=org.sqlite.JDBC
```

#### init.sql

Que sera el script que cree la base de datos
```sql
DROP TABLE IF EXISTS mediciones;

CREATE TABLE IF NOT EXISTS mediciones(
     id INTEGER PRIMARY KEY AUTOINCREMENT,
     localidad TEXT NOT NULL,
     provincia TEXT NOT NULL,
     temp_max REAL NOT NULL,
     hour_temp_max TEXT NOT NULL,
     temp_min REAL NOT NULL,
     hour_temp_min TEXT NOT NULL,
     precipitacion REAL NOT NULL,
     dia TEXT NOT NULL
);
```


## PROBLEMAS EN EL DESARROLLO DEL EJERCICIO


Se encontro que el fichero csv no estaba codificado men formato UTF-8 por lo que algunos de los caracteres no se interpretaban de la manera correcta.

Dos de las posibles soluciones fueron las siguientes:

- Crear la clase descrita anteriormente ``FileEncoding.java`` que leera el csv completo y creara un fichero con los datos codificados al formato UTF-8 para la correcta lectura.

- A la hora de leer los datos se puede coger los bytes de los String recibidos por nuestra clase de entrada preferida y crear un nuevo string con los bytes utilizando la Standarización UTF-8












