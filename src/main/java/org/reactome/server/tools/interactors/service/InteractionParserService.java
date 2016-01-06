package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.InteractionDetailsDAO;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionDetails;
import org.reactome.server.tools.interactors.model.Interactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade layer between Parser and Database.
 * It is a layer that access the DAOs and persist them all.
 * Each DAO has its own code and they don't mix each other.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class InteractionParserService {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private static InteractionParserService interactionParserService = null;

    public static InteractionParserService getInstance() {
        if (interactionParserService == null) {
            interactionParserService = new InteractionParserService();
        }

        return interactionParserService;
    }

    private InteractionParserService(){

    }

    public InteractionDAO interactionDAO = DAOFactory.createInterationDAO();
    public InteractorDAO interactorDAO = DAOFactory.createInteractorDAO();
    public InteractionDetailsDAO interactionDetailsDAO = DAOFactory.createInterationDetailsDAO();

    /** Creating a pre-sized list of interactions. Persist a batch of 1000 interactions **/
    public List<Interaction> interactions = new ArrayList<>(1000);

    /** Creating a pre-sized list of interactions. Persist a batch of 1000 interactions **/
    public List<InteractionDetails> interactionsDetails = new ArrayList<>(15);

    /**
     * Saving interactions in the DB requires 4 Steps.
     * 1-Create or Retrieve InteractorA and InteractorB
     * 2-Create Interaction and get its ID
     * 3-Create InteractionDetails in a batch inserter.
     *    (One interaction can have multiple InteractionID - interaction details holds this)
     *
     * @param interactionList
     * @throws SQLException
     */
    public void save(List<Interaction> interactionList) throws SQLException {
        logger.debug("Interactions bulk saving");

        for (Interaction interaction : interactionList) {
            Interactor interactorA = interaction.getInteractorA();
            Interactor interactorB = interaction.getInteractorB();

            /** Don't need a return, values are being set in reference **/
            logger.debug("Searching InteractorA and InteractorB in the Database.");
            interactorDAO.searchByAccessions(interactorA, interactorB);

            if(interactorA.getId() == null){
                logger.debug("InteractorA does not exist in the Database. Persist a new one [" + interactorA.getAcc() + "]");
                interactorDAO.create(interactorA);
            }

            if(interactorB.getId() == null){
                if(!interactorA.getAcc().equals(interactorB.getAcc())) { // if A is equal than B skip B creation
                    logger.debug("InteractorB does not exist in the Database. Persist a new one [" + interactorB.getAcc() + "]");
                    interactorDAO.create(interactorB);
                }else {
                    interactorB.setId(interactorA.getId());
                }
            }

            interactions.add(interaction);
        }

        /** go to db, create and set Ids. **/
        interactionDAO.create(interactions);

        for (Interaction interaction : interactions) {
            List<InteractionDetails> interactionDetailsList = interaction.getInteractionDetailsList();
            for (InteractionDetails interactionDetails : interactionDetailsList) {
                interactionDetails.setInteractionId(interaction.getId());

                interactionsDetails.add(interactionDetails);
            }
        }

        interactionDetailsDAO.create(interactionsDetails);

        /** Clean up list, for next round **/
        interactions.clear();
        interactionsDetails.clear();

        logger.debug("Interactions bulk processor done.");

    }

}
