package org.amet.repositories.amets;

import org.amet.models.Amet;
import org.amet.services.database.DataBaseManager;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de mediciones
 * @author Daniel y Diego
 */
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
