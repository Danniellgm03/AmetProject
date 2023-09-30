package org.amet.utils;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Clase que contiene métodos útiles para Amet
 * @version 1.0
 * @author Daniel y Diego
 */
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
