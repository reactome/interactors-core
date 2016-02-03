package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.InteractionResource;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractionResourceDAO {

    InteractionResource getByName(String resource) throws SQLException;

    List<InteractionResource> getAll() throws SQLException;
}
