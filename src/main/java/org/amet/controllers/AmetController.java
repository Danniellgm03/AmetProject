package org.amet.controllers;

import org.amet.models.Amet;
import org.amet.repositories.amets.AmetRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel y Diego
 * @version 1.0
 */
public class AmetController {

    /**
     * Repositorio de mediciones
     * @see AmetRepositoryImpl
     */
    private AmetRepositoryImpl ametRepository;

    /**
     * Constructor
     * @param ametRepository - Repositorio de mediciones
     */
    public AmetController(AmetRepositoryImpl ametRepository){
        this.ametRepository = ametRepository;
    }

    /**
     * Encuentra todas las mediciones de la base de datos
     * @return List  Lista de mediciones
     */
    public List<Amet> findAll(){
        try {
            return ametRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserta una medicion en la base de datos
     * @param medicion - Medicion a insertar
     * @return Amet Medicion insertada
     */
    public Amet insertMedicion(Amet medicion){
        Amet inserted = null;
        try {
            inserted = ametRepository.insert(medicion);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return inserted;
    }

    /**
     * Buscar medicion por id
     * @param id  Id de la medicion
     * @return Amet  Medicion encontrada
     */
    public Amet findById(Integer id){
        try {
            return ametRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Buscar mediciones por provincia
     * @param provincia - Provincia de la medicion
     * @return List Lista de mediciones encontradas
     */
    public List<Amet> findByProvincia(String provincia){
        try {
            return ametRepository.findByProvincia(provincia);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
