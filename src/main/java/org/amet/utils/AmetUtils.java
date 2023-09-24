package org.amet.utils;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AmetUtils {

    public static LocalDate getDayOfNameFile(String route_file){

        String filename = Path.of(route_file).getFileName().toString();
        String diaString = filename.split("Aemet")[1].split("\\.")[0];
        DateTimeFormatter formatter_day = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);
        LocalDate dia = LocalDate.parse(diaString, formatter_day);

        return dia;
    }
}
