package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.Interactor;

import java.sql.SQLException;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractorDAO extends BaseDAO<Interactor> {

    // add here something specific for Interactor
    Interactor getByAccession(String acc) throws SQLException;
}
