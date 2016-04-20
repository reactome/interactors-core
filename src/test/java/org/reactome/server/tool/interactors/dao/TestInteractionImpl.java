package org.reactome.server.tool.interactors.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactome.server.interactors.dao.InteractionDAO;
import org.reactome.server.interactors.dao.intact.StaticInteraction;
import org.reactome.server.interactors.database.InteractorsDatabase;
import org.reactome.server.interactors.exception.InvalidInteractionResourceException;
import org.reactome.server.interactors.model.Interaction;
import org.reactome.server.interactors.service.InteractionService;
import org.reactome.server.interactors.util.InteractorConstant;
import org.reactome.server.interactors.util.Toolbox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractionImpl {

    private InteractionDAO interactionDAO;

    private InteractionService interactionService;

    private final String ACCESSION = "Q13501";
    private final Long RESOURCE_ID = 1L;

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
        interactionService = new InteractionService(interactors);
    }

    @Test
    public void testCountInteraction() throws SQLException {
        List<String> accessions = new ArrayList<>();
        accessions.add(ACCESSION);

        Map<String, Integer> countingMap = interactionDAO.countByAccessions(accessions, RESOURCE_ID);
        int count = countingMap.get(ACCESSION);

        Assert.assertTrue(count > 0);
    }

    @Test
    public void testGetInteractions() throws SQLException {
        List<String> accessions = new ArrayList<>();
        accessions.add(ACCESSION);

        List<Interaction> interactions = interactionDAO.getByAcc(accessions, RESOURCE_ID, -1, -1);

        for (Interaction interaction : interactions) {
            Assert.assertTrue("Score lower than " + InteractorConstant.MINIMUM_VALID_SCORE, interaction.getIntactScore() >= InteractorConstant.MINIMUM_VALID_SCORE);
        }

        Assert.assertTrue(interactions.size() > 0);

    }

    @Test
    public void testRoundScore() {
        Assert.assertEquals("1- Score not round properly", Toolbox.roundScore(0.467866), new Double(0.468));
        Assert.assertEquals("2- Score not round properly", Toolbox.roundScore(0.457123), new Double(0.457));
        Assert.assertEquals("3- Score not round properly", Toolbox.roundScore(0.444444), new Double(0.444));
        Assert.assertEquals("4- Score not round properly", Toolbox.roundScore(1.5555), new Double(1.556));
        Assert.assertEquals("5- Score not round properly", Toolbox.roundScore(0.45555), new Double(0.456));
        Assert.assertEquals("6- Score not round properly", Toolbox.roundScore(0.4490), new Double(0.449));
        Assert.assertEquals("7- Score not round properly", Toolbox.roundScore(0.4499), new Double(0.45));
        Assert.assertEquals("8- Score not round properly", Toolbox.roundScore(0.45), new Double(0.45));
        Assert.assertEquals("8- Score not round properly", Toolbox.roundScore(0.4499999), new Double(0.45)); // ask
        Assert.assertEquals("9- Score not round properly", Toolbox.roundScore(0.4445), new Double(0.445));
        Assert.assertEquals("10- Score not round properly", Toolbox.roundScore(0.4466), new Double(0.447));

        Assert.assertEquals("11- Score not round properly", Toolbox.roundScore(0.447), new Double(0.447));
        Assert.assertEquals("12- Score not round properly", Toolbox.roundScore(0.44295776), new Double(0.443));
        Assert.assertEquals("13- Score not round properly", Toolbox.roundScore(0.44547057), new Double(0.445));
        Assert.assertEquals("14- Score not round properly", Toolbox.roundScore(0.44611502), new Double(0.446));
        Assert.assertEquals("15- Score not round properly", Toolbox.roundScore(0.44681886), new Double(0.447));
        Assert.assertEquals("16- Score not round properly", Toolbox.roundScore(0.44962552), new Double(0.45)); // ask
        Assert.assertEquals("17- Score not round properly", Toolbox.roundScore(0.9921667), new Double(0.992));
        Assert.assertEquals("18- Score not round properly", Toolbox.roundScore(0.9919758), new Double(0.992));
        Assert.assertEquals("19- Score not round properly", Toolbox.roundScore(0.98239964), new Double(0.982));
        Assert.assertEquals("20- Score not round properly", Toolbox.roundScore(0.999), new Double(0.999));

        Assert.assertEquals("21- Score not round properly", Toolbox.roundScore(0.9999), new Double(1)); // ask
    }

    @Test
    public void testGetInteractionsAndRemoveDuplicates() throws SQLException, InvalidInteractionResourceException {
        List<Interaction> interactions = interactionService.getInteractions(ACCESSION, InteractorConstant.STATIC);

        Assert.assertFalse("Interactors list is Empty", interactions.isEmpty());
        Assert.assertTrue("Interactor less than 10", interactions.size() > 10);

    }
}
