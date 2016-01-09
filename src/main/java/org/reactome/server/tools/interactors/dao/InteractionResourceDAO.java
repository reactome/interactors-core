package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.InteractionResource;

import java.sql.SQLException;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractionResourceDAO extends BaseDAO<InteractionResource> {

    // add here something specific for InteractionResource
    InteractionResource getByName(String resource) throws SQLException;
}
