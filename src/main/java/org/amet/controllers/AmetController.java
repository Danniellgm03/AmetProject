package org.amet.controllers;

import org.amet.models.Amet;
import org.amet.repositories.amets.AmetRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AmetController {

    private AmetRepositoryImpl ametRepository;

    public AmetController(AmetRepositoryImpl ametRepository){
        this.ametRepository = ametRepository;
    }

    public List<Amet> findAll(){
        try {
            return ametRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Amet insertMedicion(Amet medicion){
        Amet inserted = null;
        try {
            inserted = ametRepository.insert(medicion);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return inserted;
    }

    public Amet findById(Integer id){
        try {
            return ametRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
