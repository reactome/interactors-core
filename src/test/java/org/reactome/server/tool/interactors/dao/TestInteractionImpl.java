package org.reactome.server.tool.interactors.dao;

import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.intact.StaticInteraction;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.Interaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractionImpl {

    private InteractionDAO interactionDAO;

    @Before
    public void setUp() {
        String file = "/Users/reactome/interactors/interactors.db";
        InteractorsDatabase interactors = null;
        try {
            interactors = new InteractorsDatabase(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        interactionDAO = new StaticInteraction(interactors);
    }

    @Test
    public void testCountInteraction() throws SQLException{
        List<String> interactions = new ArrayList<>();
        interactions.add("EBI-7121510");


        List<Interaction> aaaa = interactionDAO.getByIntactId(interactions, 1L, -1, -1);

        System.out.println(aaaa);
    }

}
