package org.amet;

import org.amet.controllers.AmetController;
import org.amet.models.Amet;
import org.amet.repositories.amets.AmetRepositoryImpl;
import org.amet.services.amet.AmetCsvManager;
import org.amet.services.database.DataBaseManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        try {
            DataBaseManager.getInstance().initData( ClassLoader.getSystemResource("init.sql").getFile().toString(), false);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Path ruta = Paths.get("");
        String ruta_absoluta = ruta.toAbsolutePath().toString() + File.separator + "data" ;
        String ruta_file = ruta_absoluta + File.separator + "Aemet20171030.csv";
        AmetCsvManager managerAmet = new AmetCsvManager();
        List<Amet> amets = managerAmet.readAllCSV(ruta_file);
        managerAmet.insertAllMediciones();
        AmetController ametController = new AmetController(new AmetRepositoryImpl(DataBaseManager.getInstance()));
        List<Amet> mediciones = ametController.findAll();

    }
}