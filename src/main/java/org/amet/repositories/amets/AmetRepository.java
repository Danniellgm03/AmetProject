package org.amet.repositories.amets;

import org.amet.models.Amet;
import org.amet.repositories.base.CrudRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Daniel y Diego
 * @see org.amet.repositories.base.CrudRepository
 */
public interface AmetRepository extends CrudRepository<Amet> {

    /**
     * Encontrar medicion por id
     * @return List Lista de Mediciones
     */
    Amet findById(Integer id) throws SQLException;

    /**
     * Borrar todas las mediciones
     * @return List Lista de Mediciones
     */
    List<Amet> deleteAll();

}
