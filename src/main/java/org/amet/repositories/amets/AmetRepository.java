package org.amet.repositories.amets;

import org.amet.models.Amet;
import org.amet.repositories.base.CrudRepository;

import java.sql.SQLException;
import java.util.List;

public interface AmetRepository extends CrudRepository<Amet> {

    Amet findById(Integer id) throws SQLException;

    List<Amet> deleteAll();

}
