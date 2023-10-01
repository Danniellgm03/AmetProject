package org.amet;

import org.amet.controllers.AmetController;
import org.amet.models.Amet;
import org.amet.repositories.amets.AmetRepositoryImpl;
import org.amet.services.amet.AmetCsvManager;
import org.amet.services.database.DataBaseManager;
import org.amet.utils.files.JsonFileAmet;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase principal del proyecto
 * @version 1.0
 * @author Daniel y Diego
 */
public class Main {

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

    /**
     * Método principal
     * @param args Argumentos
     */
    public static void main(String[] args) {

        try {
            DataBaseManager.getInstance().initData(ClassLoader.getSystemResource("init.sql").getFile(), false);
        } catch (FileNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        Path ruta = Paths.get("");
        String ruta_absoluta = ruta.toAbsolutePath().toString() + File.separator + "data";

        AmetCsvManager managerAmet = new AmetCsvManager();

        String[] files = {"UTF8Aemet20171030.csv", "UTF8Aemet20171031.csv", "UTF8Aemet20171029.csv"};
        for (String file: files) {
            String ruta_file = ruta_absoluta + File.separator + file ;
            managerAmet.readAllCSV(ruta_file);
        }

        managerAmet.insertAllMediciones();
        AmetController ametController = new AmetController(new AmetRepositoryImpl(DataBaseManager.getInstance()));
        List<Amet> mediciones = ametController.findAll();
        //System.out.println(mediciones);


        System.out.println("¿Dónde se dio la temperatura máxima y mínima total en cada uno de los días?.");
        Map<String, List<Amet>> separed_by_day= mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.toList()
                ));
        separed_by_day.entrySet().stream()
                .map((value) -> value.getValue().stream().max(Comparator.comparingDouble(Amet::getTemp_max)))
                .toList().forEach((elem) -> System.out.println("El maximo del dia : " + elem.get().getDia() + " es " + elem.get().getTemp_max()));

        separed_by_day.entrySet().stream()
                .map((value) -> value.getValue().stream().min(Comparator.comparingDouble(Amet::getTemp_min)))
                .toList().forEach((elem) -> System.out.println("El minimo del dia : " + elem.get().getDia() + " es " + elem.get().getTemp_min()));
        System.out.println();

        System.out.println("Máxima temperatura agrupado por provincias y día.");
        var max_per_dayAndProvincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(Comparator.comparingDouble(Amet::getTemp_max)),
                                        (a) -> a.map(Amet::getTemp_max).orElse(null)
                                )
                        )

                ));
        System.out.println(max_per_dayAndProvincia);
        System.out.println();

       System.out.println("Mínima temperatura agrupado por provincias y día.");
        var min_per_dayAndProvincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.collectingAndThen(
                                        Collectors.minBy(Comparator.comparingDouble(Amet::getTemp_min)),
                                        (a) -> a.map(Amet::getTemp_min).orElse(null)
                                )
                        )

                ));
        System.out.println(min_per_dayAndProvincia);
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
        System.out.println(avg_per_dayAndProvincia);
        System.out.println();

        System.out.println("Precipitación máxima por días y dónde se dio");
        var precipitacion_max_perDay = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.maxBy(Comparator.comparingDouble(Amet::getPrecipitacion))
                ));
        System.out.println(precipitacion_max_perDay);
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
        System.out.println(precipitacion_avg_perDay_Provincia);
        System.out.println();


        System.out.println("Lugares donde ha llovido agrupado por provincias y día.");
        var lugarllovido_groupby_provincias_dia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        Amet::getProvincia,
                        Collectors.groupingBy(
                                (x) -> x.getDia().toString(),
                                Collectors.filtering((x) -> x.getPrecipitacion() > 0, Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        (x) -> x.stream().map(Amet::getLocalidad).toList()
                                ))
                        )
                ));
        System.out.println(lugarllovido_groupby_provincias_dia);
        System.out.println();

        System.out.println("Lugar donde más ha llovido.");
        var lugar_donde_mas_llovido = mediciones.stream()
                .max(Comparator.comparingDouble(Amet::getPrecipitacion));
        System.out.println(lugar_donde_mas_llovido);
        System.out.println();



        datosByProvincia("A Coruña", mediciones);
        try {
            JsonFileAmet.exportJsonAmetProvincia("A Coruña", mediciones, ruta_absoluta);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}

