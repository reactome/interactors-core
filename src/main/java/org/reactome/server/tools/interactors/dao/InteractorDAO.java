package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.Interactor;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractorDAO extends BaseDAO<Interactor> {

    // add here something specific for Interactor
    Interactor getByAccession(String acc) throws SQLException;

    void searchByAccessions(Interactor interactorA, Interactor interactorB) throws SQLException;

    boolean exists(String acc) throws SQLException;
}
