package org.reactome.server.tools.interactors.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface BaseDAO<T> {

    T create(T t) throws SQLException;

    boolean update(T t) throws SQLException;

    T getById(String id) throws SQLException;

    List<T> getAll() throws SQLException;

    boolean delete(String id) throws SQLException;

}
