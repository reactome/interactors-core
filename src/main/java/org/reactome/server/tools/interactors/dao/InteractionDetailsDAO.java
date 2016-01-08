package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.InteractionDetails;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface InteractionDetailsDAO extends BaseDAO<InteractionDetails> {
    // add here something specific for Interactor

    boolean create(List<InteractionDetails> interactionDetails) throws SQLException;

    List<InteractionDetails> getByInteraction(Long interactionId) throws SQLException;

}
