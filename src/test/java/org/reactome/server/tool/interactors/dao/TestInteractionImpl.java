package org.reactome.server.tool.interactors.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.intact.StaticInteraction;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.Interaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractionImpl {

    private InteractionDAO interactionDAO;

    private final String ACCESSION = "Q13501";
    private final Long RESOURCE_ID = 1L;

    @Before
    public void setUp() {
        String file = "/Users/reactome/interactors/interactors3.db";
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
        List<String> accessions = new ArrayList<>();
        accessions.add(ACCESSION);

        Map<String, Integer> countingMap = interactionDAO.countByAccessions(accessions, RESOURCE_ID);
        int count = countingMap.get(ACCESSION);

        Assert.assertTrue(count > 0);
    }

    @Test
    public void testGetInteractions() throws SQLException{
        List<String> accessions = new ArrayList<>();
        accessions.add(ACCESSION);

        List<Interaction> interactions = interactionDAO.getByAcc(accessions, RESOURCE_ID, -1, -1);

        for (Interaction interaction : interactions) {
            Assert.assertTrue("Score lower than 0.45", interaction.getIntactScore() >= 0.45);
        }

        Assert.assertTrue(interactions.size() > 0);

    }



}
