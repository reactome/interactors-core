package org.reactome.server.interactors.dao;

import org.reactome.server.interactors.model.InteractorResource;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractorResourceDAO {

    List<InteractorResource> getAll() throws SQLException;
}
