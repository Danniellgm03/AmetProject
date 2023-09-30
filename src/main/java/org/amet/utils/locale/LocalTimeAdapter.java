package org.amet.utils.locale;

import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.time.LocalTime;

/**
 * Clase que sirve para adaptar la clase LocalTime para que pueda ser interpretado por GSON
 * @version 1.0
 * @see com.google.gson.TypeAdapter
 * @author Daniel y Diego
 */
public class LocalTimeAdapter  extends TypeAdapter<LocalTime> {

    /**
     * Metodo que escribe un objeto LocalDate en un JsonWriter
     * @param jsonWriter JsonWriter
     * @param localTime LocalDate
     */
    @Override
    public void write(com.google.gson.stream.JsonWriter jsonWriter, LocalTime localTime) throws IOException {
        jsonWriter.value(localTime.toString());
    }

    /**
     * Metodo que lee un objeto LocalDate de un JsonReader
     * @param jsonReader JsonReader
     * @return LocalDate
     */
    @Override
    public LocalTime read(com.google.gson.stream.JsonReader jsonReader) throws java.io.IOException {
        return LocalTime.parse(jsonReader.nextString());
    }
}