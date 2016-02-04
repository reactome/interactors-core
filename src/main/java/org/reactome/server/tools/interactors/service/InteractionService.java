package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.InteractionDetailsDAO;
import org.reactome.server.tools.interactors.dao.InteractionResourceDAO;
import org.reactome.server.tools.interactors.dao.intact.StaticInteractionDetails;
import org.reactome.server.tools.interactors.dao.intact.StaticInteraction;
import org.reactome.server.tools.interactors.dao.intact.StaticInteractionResource;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.exception.InvalidInteractionResourceException;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.reactome.server.tools.interactors.util.Toolbox;

import java.sql.SQLException;
import java.util.*;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

@SuppressWarnings("unused")
public class InteractionService {

    private InteractionDAO interactionDAO;
    private InteractionDetailsDAO interactionDetailsDAO;
    private InteractionResourceDAO interactionResourceDAO;

    public InteractionService(InteractorsDatabase database){
        this.interactionDAO = new StaticInteraction(database);
        this.interactionDetailsDAO = new StaticInteractionDetails(database);
        this.interactionResourceDAO = new StaticInteractionResource(database);
    }

    /**
     * Get all interactions of a given accession and resource
     * @return Map of accession as key and its interactions
     * @throws InvalidInteractionResourceException
     * @throws SQLException
     */
    public Map<String, List<Interaction>> getInteractions(String acc, String resource) throws InvalidInteractionResourceException, SQLException {
        List<String> accs = new ArrayList<>(1);
        accs.add(acc);

        return getInteractions(accs,resource, -1, -1);
    }

    /**
     * Get all interactions of a given accession and resource
     *
     * @return Map of accession as key and its interactions
     * @throws InvalidInteractionResourceException
     * @throws SQLException
     */
    public Map<String, List<Interaction>> getInteractions(Collection<String> accs, String resource) throws InvalidInteractionResourceException, SQLException {
        return getInteractions(accs,resource, -1, -1);
    }

    /**
     * Get paginated interactions of a given accession and resource
     * @return Map of accession as key and its interactions
     * @throws InvalidInteractionResourceException
     * @throws SQLException
     */
    public Map<String, List<Interaction>> getInteractions(String acc, String resource, Integer page, Integer pageSize) throws InvalidInteractionResourceException, SQLException {
        List<String> accs = new ArrayList<>(1);
        accs.add(acc);

        return getInteractions(accs,resource, page, pageSize);
    }

    /**
     * Get interactions of a given list of accession and resource
     * @return Map of accession as key and its interactions
     * @throws InvalidInteractionResourceException
     * @throws SQLException
     */
    public Map<String, List<Interaction>> getInteractions(Collection<String> accs, String resource, Integer page, Integer pageSize) throws InvalidInteractionResourceException, SQLException {

        InteractionResource interactionResource = interactionResourceDAO.getByName(resource);
        if(interactionResource == null){
            throw new InvalidInteractionResourceException();
        }

        Map<String, List<Interaction>> interactionMaps = new HashMap<>();
        for (String acc : accs) {
            List<Interaction> interactions = interactionDAO.getByAcc(acc, interactionResource.getId(), page, pageSize);

            // Set details
            for (Interaction interaction : interactions) {
                // TODO: pay attention here - maybe this method drains the performance. It will make a lot of queries in the DB
                interaction.setInteractionDetailsList(interactionDetailsDAO.getByInteraction(interaction.getId()));
            }

            interactions = Toolbox.removeDuplicatedInteractor(interactions);

            Collections.sort(interactions);
            Collections.reverse(interactions);

            interactionMaps.put(acc, interactions);

        }

        return interactionMaps;

    }

    /**
     * Count interaction by accession
     * @return Map of accession and count
     * @throws InvalidInteractionResourceException
     * @throws SQLException
     */
    public Map<String, Integer> countInteractionsByAccession(String acc, String resource) throws InvalidInteractionResourceException, SQLException {
        List<String> accs = new ArrayList<>(1);
        accs.add(acc);

        return countInteractionsByAccessions(accs, resource);
    }

    /**
     * Count interaction by accession list
     * @return Map of accession and count
     * @throws InvalidInteractionResourceException
     * @throws SQLException
     */
    public Map<String, Integer> countInteractionsByAccessions(Collection<String> accs, String resource) throws InvalidInteractionResourceException, SQLException {

        InteractionResource interactionResource = interactionResourceDAO.getByName(resource);
        if(interactionResource == null){
            throw new InvalidInteractionResourceException();
        }

        return interactionDAO.countByAccessions(accs, interactionResource.getId());

    }

}
