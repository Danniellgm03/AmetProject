package org.amet;

import netscape.javascript.JSObject;
import org.amet.controllers.AmetController;
import org.amet.models.Amet;
import org.amet.repositories.amets.AmetRepositoryImpl;
import org.amet.services.amet.AmetCsvManager;
import org.amet.services.database.DataBaseManager;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        try {
            DataBaseManager.getInstance().initData( ClassLoader.getSystemResource("init.sql").getFile().toString(), false);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Path ruta = Paths.get("");
        String ruta_absoluta = ruta.toAbsolutePath().toString() + File.separator + "data" ;
        String ruta_file = ruta_absoluta + File.separator + "Aemet20171030.csv";
        AmetCsvManager managerAmet = new AmetCsvManager();
        List<Amet> amets = managerAmet.readAllCSV(ruta_file);
        managerAmet.insertAllMediciones();
        AmetController ametController = new AmetController(new AmetRepositoryImpl(DataBaseManager.getInstance()));
        List<Amet> mediciones = ametController.findAll();

        System.out.println("¿Dónde se dio la temperatura máxima y mínima total en cada uno de los días?.");
        Map<String, List<Amet>> separed_by_day= mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.toList()
                ));

        separed_by_day.entrySet().stream()
                .map((value) -> value.getValue().stream().max(Comparator.comparingDouble(Amet::getTemp_max)))
                .toList().forEach((elem) -> System.out.println("El maximo del dia : " + elem.get().getDia() + " es " + elem.get().toString()));

        separed_by_day.entrySet().stream()
                .map((value) -> value.getValue().stream().min(Comparator.comparingDouble(Amet::getTemp_min)))
                .toList().forEach((elem) -> System.out.println("El minimo del dia : " + elem.get().getDia() + " es " + elem.get().toString()));
        System.out.println();

        System.out.println("Máxima temperatura agrupado por provincias y día.");
        var max_per_dayAndProvincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.maxBy(Comparator.comparingDouble(Amet::getTemp_max))
                        )

                ));
        System.out.println();

        System.out.println("Mínima temperatura agrupado por provincias y día.");
        var min_per_dayAndProvincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.minBy(Comparator.comparingDouble(Amet::getTemp_min))
                        )

                ));
        System.out.println();

        System.out.println("Medía de temperatura agrupado por provincias y día.");
        var avg_per_dayAndProvincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.averagingDouble((amet) -> amet.getTemp_min() + amet.getTemp_max())
                        )

                ));
        System.out.println();

        System.out.println("Precipitación máxima por días y dónde se dio");
        var precipitacion_max_perDay = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.maxBy(Comparator.comparingDouble(Amet::getPrecipitacion))
                ));
        System.out.println();

        System.out.println("Precipitación media por provincias y día.");
        var precipitacion_avg_perDay_Provincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.averagingDouble(Amet::getPrecipitacion)
                        )
                ));
        System.out.println();


        System.out.println("Lugares donde ha llovido agrupado por provincias y día.");
        var lugarllovido_groupby_provincias_dia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        Amet::getProvincia,
                        Collectors.groupingBy(
                                (x) -> x.getDia().toString(),
                                Collectors.filtering((x) -> x.getPrecipitacion() > 0, Collectors.toList())
                        )
                ));
        System.out.println();

        System.out.println("Lugar donde más ha llovido.");
        var lugar_donde_mas_llovido = mediciones.stream()
                .max(Comparator.comparingDouble(Amet::getPrecipitacion));
        System.out.println();


        System.out.println("Datos de las provincia de Madrid (debe funcionar para cualquier provincia)");
        var groupingby_provincia = mediciones.stream()
                .filter((x) -> x.getProvincia().equals("Madrid"))
                .collect(Collectors.groupingBy(
                        (x) -> x.getDia().toString(),
                        Collectors.toList()
                ));

        System.out.println(groupingby_provincia);
        var maximo_groupingby = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .max(Comparator.comparingDouble(Amet::getTemp_max))
                ));

        var minimo_groypinby = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .min(Comparator.comparingDouble(Amet::getTemp_min))
                ));

        var media_max_temp = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.averagingDouble(Amet::getTemp_max))
                ));

        var media_min_temp = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.averagingDouble(Amet::getTemp_min))
                ));

        var maximo_groupingby_precipitacion = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .max(Comparator.comparingDouble(Amet::getPrecipitacion))
                ));
        System.out.println(maximo_groupingby_precipitacion);

        var media_precipitacion = groupingby_provincia.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.averagingDouble(Amet::getPrecipitacion))
                ));


        JSONObject json = new JSONObject();
        json.put("provincia", "Madrid");
        JSONObject json_max_temps = new JSONObject();
        json_max_temps.put("temp_max", maximo_groupingby.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.comparingDouble(Amet::getTemp_max)).get().getTemp_max());
        json_max_temps.put("localidad",  maximo_groupingby.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.comparingDouble(Amet::getTemp_max)).get().getLocalidad());
        json.put("temperatura_maxima", json_max_temps);

        JSONObject json_min_temps = new JSONObject();
        json_min_temps.put("temp_min", minimo_groypinby.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.comparingDouble(Amet::getTemp_max)).get().getTemp_min());
        json_min_temps.put("localidad",  minimo_groypinby.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.comparingDouble(Amet::getTemp_max)).get().getLocalidad());
        json.put("temperatura_minima", json_min_temps);
        json.put("avg_temp_max", media_max_temp.values());
        json.put("avg_temp_min", media_min_temp.values());

        JSONObject json_precipitacion = new JSONObject();
        json_precipitacion.put("max_precipitacion", maximo_groupingby_precipitacion.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.comparingDouble(Amet::getTemp_max)).get().getPrecipitacion());
        json_precipitacion.put("localidad", maximo_groupingby_precipitacion.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.comparingDouble(Amet::getTemp_max)).get().getLocalidad());

        json.put("precipitacion_estadisticas", json_precipitacion);
        json.put("avg_precipitacion", media_precipitacion.values());
        System.out.println(json.toString());

    }
}