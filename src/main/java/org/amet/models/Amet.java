package org.amet.models;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@Getter
@Setter
public class Amet {

    private String localidad;
    private String provincia;
    private double temp_max;
    private LocalTime hour_temp_max;
    private double temp_min;
    private LocalTime hour_temp_min;
    private double precipitacion;
    private LocalDate dia;

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
