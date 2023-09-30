package org.amet.repositories.amets;

import org.amet.models.Amet;
import org.amet.services.database.DataBaseManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AmetRepositoryImpl implements AmetRepository {
    private final DataBaseManager dataBaseManager;

    public AmetRepositoryImpl(DataBaseManager database){
        dataBaseManager = database;
    }

    @Override
    public List<Amet> findAll() throws SQLException {
        dataBaseManager.openConnection();
        String sql = "SELECT * FROM mediciones";
        var result = dataBaseManager.select(sql).orElseThrow(() -> new SQLException("Error al obtener todas las mediciones"));

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

    @Override
    public Amet update(Amet entity) {
        return null;
    }

    @Override
    public Amet delete(Amet entity) {
        return null;
    }

    @Override
    public List<Amet> deleteAll() {
        return null;
    }
}
