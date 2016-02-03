package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.InteractorResource;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractorResourceDAO {

    List<InteractorResource> getAll() throws SQLException;
}
