package org.amet.utils.locale;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Clase que sirve para adaptar la clase LocalDate para que pueda ser interpretado por GSON
 * @version 1.0
 * @see com.google.gson.TypeAdapter
 * @author Daniel y Diego
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    /**
     * Metodo que escribe un objeto LocalDate en un JsonWriter
     * @param jsonWriter JsonWriter
     * @param localDate LocalDate
     */
    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        jsonWriter.value(localDate.toString());
    }

    /**
     * Metodo que lee un objeto LocalDate de un JsonReader
     * @param jsonReader JsonReader
     * @return LocalDate
     */
    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        return LocalDate.parse(jsonReader.nextString());
    }
}
