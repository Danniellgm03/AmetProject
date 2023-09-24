package org.amet.repositories.base;

import java.sql.SQLException;
import java.util.List;

public interface CrudRepository<T> {

    List<T> findAll() throws SQLException;

    T findById(Integer id) throws SQLException;

    T insert(T entity) throws SQLException;

    T update(T entity)  throws SQLException ;

    T delete(T entity)  throws SQLException ;

}
