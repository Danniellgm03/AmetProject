package org.amet.models;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase que representa una medicion
 * @author Daniel y Diego
 * @version 1.0
 * @see lombok
 */
@Data
@Builder
@Getter
@Setter
public class Amet {

    /**
     * Localidad de la medicion
     * @see String
     */
    private String localidad;

    /**
     * Provincia de la medicion
     * @see String
     */
    private String provincia;

    /**
     * Temperatura maxima de la medicion
     * @see double
     */
    private double temp_max;

    /**
     * Hora de la temperatura maxima de la medicion
     * @see LocalTime
     */
    private LocalTime hour_temp_max;

    /**
     * Temperatura minima de la medicion
     * @see double
     */
    private double temp_min;

    /**
     * Hora de la temperatura minima de la medicion
     * @see LocalTime
     */
    private LocalTime hour_temp_min;

    /**
     * Precipitacion de la medicion
     * @see double
     */
    private double precipitacion;

    /**
     * Dia de la medicion
     * @see LocalDate
     */
    private LocalDate dia;

    /**
     * To string
     * @return String
     */
    @Override
    public String toString() {
        return "Amet{" +
                "localidad='" + localidad + '\'' +
                ", provincia='" + provincia + '\'' +
                ", temp_max=" + temp_max +
                ", hour_temp_max=" + hour_temp_max +
                ", temp_min=" + temp_min +
                ", hour_temp_min=" + hour_temp_min +
                ", precipitacion=" + precipitacion +
                ", dia=" + dia +
                '}';
    }
}
