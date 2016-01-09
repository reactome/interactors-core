package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.InteractionDetailsDAO;
import org.reactome.server.tools.interactors.dao.InteractionResourceDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.exception.InvalidInteractionResourceException;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionService {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private static InteractionService interactionService = null;

    private InteractionDAO interactionDAO = DAOFactory.createInterationDAO();
    private InteractionDetailsDAO interactionDetailsDAO = DAOFactory.createInterationDetailsDAO();
    private InteractionResourceDAO interactionResourceDAO = DAOFactory.createInterationResourceDAO();

    public static InteractionService getInstance() {
        if (interactionService == null) {
            interactionService = new InteractionService();
        }

        return interactionService;
    }

    private InteractionService(){

    }

    /**
     * Get interactions of a given accession and resource
     * @param acc
     * @param resource
     * @return Map of accession as key and its interactions
     * @throws InvalidInteractionResourceException
     * @throws SQLException
     */
    public Map<String, List<Interaction>> getInteractions(String acc, String resource) throws InvalidInteractionResourceException, SQLException {
        List<String> accs = new ArrayList<>(1);
        accs.add(acc);

        return getInteractions(accs,resource);
    }

    /**
     * Get interactions of a given list of accession and resource
     * @param accs
     * @param resource
     * @return Map of accession as key and its interactions
     * @throws InvalidInteractionResourceException
     * @throws SQLException
     */
    public Map<String, List<Interaction>> getInteractions(Collection<String> accs, String resource) throws InvalidInteractionResourceException, SQLException {

        InteractionResource interactionResource = interactionResourceDAO.getByName(resource);
        if(interactionResource == null){
            throw new InvalidInteractionResourceException();
        }

        Map<String, List<Interaction>> interactionMaps = new HashMap<>();
        for (String acc : accs) {
            List<Interaction> interactions = interactionDAO.getByAcc(acc, interactionResource.getId());

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
