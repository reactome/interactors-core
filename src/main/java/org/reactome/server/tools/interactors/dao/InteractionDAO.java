package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.Interaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractionDAO extends BaseDAO<Interaction> {
    // add here something specific for Interaction

    boolean create(List<Interaction> interaction) throws SQLException;

    List<Interaction> getByAcc(String acc, String resourceId) throws SQLException;

    List<Interaction> getByAcc(List<String> acc, String resourceId) throws SQLException;

}
