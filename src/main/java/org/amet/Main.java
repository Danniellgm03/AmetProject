package org.amet;

import org.amet.controllers.AmetController;
import org.amet.models.Amet;
import org.amet.repositories.amets.AmetRepositoryImpl;
import org.amet.services.amet.AmetCsvManager;
import org.amet.services.database.DataBaseManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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


        //1
//        Map<String, List<Amet>> separed_by_day= mediciones.stream()
//                .collect(Collectors.groupingBy(
//                        (a) -> a.getDia().toString(),
//                        Collectors.toList()
//                ));
//
//        separed_by_day.entrySet().stream()
//                .map((value) -> value.getValue().stream().max(Comparator.comparingDouble(Amet::getTemp_max)))
//                .toList().forEach((elem) -> System.out.println("El maximo del dia : " + elem.get().getDia() + " es " + elem.get().toString()));
//
//        separed_by_day.entrySet().stream()
//                .map((value) -> value.getValue().stream().min(Comparator.comparingDouble(Amet::getTemp_min)))
//                .toList().forEach((elem) -> System.out.println("El minimo del dia : " + elem.get().getDia() + " es " + elem.get().toString()));


        //2
        var max_per_dayAndProvincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.maxBy(Comparator.comparingDouble(Amet::getTemp_max))
                        )

                ));

        //3
        var min_per_dayAndProvincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.minBy(Comparator.comparingDouble(Amet::getTemp_min))
                        )

                ));

        //4
        var avg_per_dayAndProvincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.averagingDouble((amet) -> amet.getTemp_min() + amet.getTemp_max())
                        )

                ));
//        System.out.println(avg_per_dayAndProvincia);


        //5
        var precipitacion_max_perDay = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.maxBy(Comparator.comparingDouble(Amet::getPrecipitacion))
                ));
//        System.out.println(precipitacion_max_perDay);

        //6
        var precipitacion_avg_perDay_Provincia = mediciones.stream()
                .collect(Collectors.groupingBy(
                        (a) -> a.getDia().toString(),
                        Collectors.groupingBy(
                                Amet::getProvincia,
                                Collectors.averagingDouble(Amet::getPrecipitacion)
                        )
                ));
//        System.out.println(precipitacion_avg_perDay_Provincia);

        //7


        //8
        var groupingby_provincia = mediciones.stream()
                .filter((x) -> x.getProvincia().equals("Madrid"))
                .collect(Collectors.groupingBy(
                        (x) -> x.getDia().toString(),
                        Collectors.toList()
                ));

        var json_prueba = groupingby_provincia.entrySet().stream().map((x) -> {
            String key = x.getKey();
            var values = x.getValue();

            List<Object> data = new ArrayList<>();

            var max_temp = values.stream()
                    .max(Comparator.comparingDouble(Amet::getTemp_max));

            var min_temp = values.stream()
                    .max(Comparator.comparingDouble(Amet::getTemp_min));

            var media_max_temp = values.stream()
                    .collect(Collectors.averagingDouble(Amet::getTemp_max));

            var media_min_temp = values.stream()
                    .collect(Collectors.averagingDouble(Amet::getTemp_min));

            var precipitacion_max = values.stream()
                    .max(Comparator.comparingDouble(Amet::getPrecipitacion));

            var precipitacion_avg = values.stream()
                    .collect(Collectors.averagingDouble(Amet::getPrecipitacion));

            data.add(max_temp);
            data.add(min_temp);
            data.add(media_max_temp);
            data.add(media_min_temp);
            data.add(precipitacion_max);
            data.add(precipitacion_avg);
            return data;
        });
        System.out.println(json_prueba.toList());

    }
}