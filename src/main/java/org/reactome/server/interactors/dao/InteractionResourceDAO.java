package org.reactome.server.interactors.dao;

import org.reactome.server.interactors.model.InteractionResource;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractionResourceDAO {

    InteractionResource getByName(String resource) throws SQLException;

    List<InteractionResource> getAll() throws SQLException;
}
