package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.InteractionDetailsDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.model.Interaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionService {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private static InteractionService interactionService = null;

    private InteractionDAO interactionDAO = DAOFactory.createInterationDAO();
    private InteractionDetailsDAO interactionDetailsDAO = DAOFactory.createInterationDetailsDAO();

    public static InteractionService getInstance() {
        if (interactionService == null) {
            interactionService = new InteractionService();
        }

        return interactionService;
    }

    private InteractionService(){

    }

    public Map<String, List<Interaction>> getInteractions(String acc, String resourceId) throws SQLException {
        List<String> accs = new ArrayList<>(1);
        accs.add(acc);

        return getInteractions(accs,resourceId);
    }

    public Map<String, List<Interaction>> getInteractions(List<String> accs, String resourceId) throws SQLException {
        Map<String, List<Interaction>> interactionMaps = new HashMap<>();
        for (String acc : accs) {
            List<Interaction> interactions = interactionDAO.getByAcc(acc, resourceId);

            // Set details
            for (Interaction interaction : interactions) {
                // TODO: pay attention here - maybe this method drains the performance. It will make a lot of queries in the DB
                interaction.setInteractionDetailsList(interactionDetailsDAO.getByInteraction(interaction.getId()));
            }


            interactionMaps.put(acc, interactions);

        }

        return interactionMaps;

    }

}
