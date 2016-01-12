package org.reactome.server.tool.interactors.dao;

import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.model.Interaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractionImpl {

    private InteractionDAO interactionDAO;
    private InteractorDAO interactorDAO;
    private String interactionID;

    @Before
    public void setUp() {
        interactionDAO = DAOFactory.createInterationDAO();
    }

    @Test
    public void testCountInteraction() throws SQLException{
        List<String> interactions = new ArrayList<>();
        interactions.add("EBI-7121510");


        List<Interaction> aaaa = interactionDAO.getByIntactId(interactions, 1L, -1, -1);

        System.out.println(aaaa);
    }

//    @Test
//    public void testInteractorDAO() {
//        try {
//            Interactor interactorA = new Interactor();
//            interactorA.setId(interactionID); // It is already being set in the constructor. Setting here as testing purpose
//            interactorA.setName("OR2CR1");
//            interactorA.setProteinId("O95371");
//            interactorA.setPsicquicResource(getPsicquicResource().getId());
//
//            boolean created = interactorDAO.create(interactorA);
//
//            assertTrue(created);
//
//            interactorID = interactor.getId();
//
//            /** Get from DB **/
//            Interactor dbInteractor = interactorDAO.getById(interactorID);
//            assertNotNull(dbInteractor);
//            assertEquals(dbInteractor.getId(), interactorID);
//
//            boolean deleted = interactorDAO.delete(interactorID);
//
//            assertTrue("Could not delete the Interactor.", deleted);
//
//        } catch (SQLException e) {
//            fail(e.getMessage());
//        }
//    }


}
