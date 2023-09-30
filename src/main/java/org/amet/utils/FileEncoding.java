package org.amet.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona la codificación de los ficheros
 * de entrada y salida
 * @version 1.0
 * @author Daniel y Diego
 */
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
