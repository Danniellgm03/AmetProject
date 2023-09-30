package org.amet.utils.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.amet.models.Amet;
import org.amet.utils.locale.LocalDateAdapter;
import org.amet.utils.locale.LocalTimeAdapter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Clase que gestiona la exportacion de datos a ficheros JSON
 * @version 1.0
 * @author Daniel y Diego
 */
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
        System.out.println("Exportando a " + path);
        System.out.println(Path.of(path));
        System.out.println(provincia);
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
