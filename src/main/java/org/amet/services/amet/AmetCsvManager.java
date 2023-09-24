package org.amet.services.amet;



import org.amet.models.Amet;
import org.amet.utils.AmetUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class AmetCsvManager {

    private List<Amet> mediciones = new ArrayList<>();
    public List<Amet> readAllCSV(String route_file){
        LocalDate dia = AmetUtils.getDayOfNameFile(route_file);
        System.out.println(dia);
        try {
            List<String> lines =  Files.newBufferedReader(Paths.get(route_file), StandardCharsets.ISO_8859_1 ).lines().toList();

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



}
