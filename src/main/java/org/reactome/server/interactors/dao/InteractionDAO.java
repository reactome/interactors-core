package org.reactome.server.interactors.dao;

import org.reactome.server.interactors.model.Interaction;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractionDAO {

    boolean create(List<Interaction> interaction) throws SQLException;

    List<Interaction> getByAcc(String acc, Long resourceId, Integer page, Integer pageSize) throws SQLException;

    List<Interaction> getByAcc(List<String> acc, Long resourceId, Integer page, Integer pageSize) throws SQLException;

    Map<String, Integer> countByAccessions(Collection<String> accs, Long resourceId) throws SQLException;

}