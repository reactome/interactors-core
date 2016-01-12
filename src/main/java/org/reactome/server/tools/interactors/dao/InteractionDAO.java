package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.Interaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractionDAO extends BaseDAO<Interaction> {
    // add here something specific for Interaction

    boolean create(List<Interaction> interaction) throws SQLException;

    List<Interaction> getByAcc(String acc, Long resourceId, Integer page, Integer pageSize) throws SQLException;
    List<Interaction> getByIntactId(String intactId, Long resourceId, Integer page, Integer pageSize) throws SQLException;

    List<Interaction> getByAcc(List<String> acc, Long resourceId, Integer page, Integer pageSize) throws SQLException;
    List<Interaction> getByIntactId(List<String> intactIdList, Long resourceId, Integer page, Integer pageSize) throws SQLException;

    Map<String, Integer> countByAccesssions(Collection<String> accs, Long resourceId) throws SQLException;

}