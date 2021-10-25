package org.reactome.server.tool.interactors.dao;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactome.server.interactors.dao.InteractionDAO;
import org.reactome.server.interactors.dao.intact.StaticInteraction;
import org.reactome.server.interactors.database.InteractorsDatabase;
import org.reactome.server.interactors.exception.InvalidInteractionResourceException;
import org.reactome.server.interactors.model.Interaction;
import org.reactome.server.interactors.service.InteractionService;
import org.reactome.server.interactors.util.InteractorConstant;
import org.reactome.server.interactors.util.Toolbox;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractionImpl {

    private InteractionDAO interactionDAO;

    private InteractionService interactionService;

    private final String ACCESSION = "UniProt:Q13501";
    private final Long RESOURCE_ID = 1L;

    @BeforeEach
    public void setUp() throws IOException {
        Properties prop = new Properties();
        InteractorsDatabase interactors = null;
        try {
            InputStream is = TestInteractionImpl.class.getResourceAsStream("/db.properties");
            prop.load(is);
            interactors = new InteractorsDatabase(prop.getProperty("database"));
        } catch (SQLException | IOException e) {
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

        Assertions.assertTrue(count > 0);
    }

    @Test
    public void testGetInteractions() throws SQLException {
        List<String> accessions = new ArrayList<>();
        accessions.add(ACCESSION);

        List<Interaction> interactions = interactionDAO.getByAcc(accessions, RESOURCE_ID, -1, -1);

        for (Interaction interaction : interactions) {
            Assertions.assertTrue(interaction.getIntactScore() >= InteractorConstant.MINIMUM_VALID_SCORE, "Score lower than " + InteractorConstant.MINIMUM_VALID_SCORE);
        }

        Assertions.assertTrue(interactions.size() > 0);

    }

    @Test
    public void testRoundScore() {
        Assertions.assertEquals(Double.parseDouble("1- Score not round properly"), Toolbox.roundScore(0.467866), 0.468);
        Assertions.assertEquals(Double.parseDouble("2- Score not round properly"), Toolbox.roundScore(0.457123), 0.457);
        Assertions.assertEquals(Double.parseDouble("3- Score not round properly"), Toolbox.roundScore(0.444444), 0.444);
        Assertions.assertEquals(Double.parseDouble("4- Score not round properly"), Toolbox.roundScore(1.5555), 1.556);
        Assertions.assertEquals(Double.parseDouble("5- Score not round properly"), Toolbox.roundScore(0.45555), 0.456);
        Assertions.assertEquals(Double.parseDouble("6- Score not round properly"), Toolbox.roundScore(0.4490), 0.449);
        Assertions.assertEquals(Double.parseDouble("7- Score not round properly"), Toolbox.roundScore(0.4499), 0.45);
        Assertions.assertEquals(Double.parseDouble("8- Score not round properly"), Toolbox.roundScore(0.45), 0.45);
        Assertions.assertEquals(Double.parseDouble("8- Score not round properly"), Toolbox.roundScore(0.4499999), 0.45); // ask
        Assertions.assertEquals(Double.parseDouble("9- Score not round properly"), Toolbox.roundScore(0.4445), 0.445);
        Assertions.assertEquals(Double.parseDouble("10- Score not round properly"), Toolbox.roundScore(0.4466), 0.447);

        Assertions.assertEquals(Double.parseDouble("11- Score not round properly"), Toolbox.roundScore(0.447), 0.447);
        Assertions.assertEquals(Double.parseDouble("12- Score not round properly"), Toolbox.roundScore(0.44295776), 0.443);
        Assertions.assertEquals(Double.parseDouble("13- Score not round properly"), Toolbox.roundScore(0.44547057), 0.445);
        Assertions.assertEquals(Double.parseDouble("14- Score not round properly"), Toolbox.roundScore(0.44611502), 0.446);
        Assertions.assertEquals(Double.parseDouble("15- Score not round properly"), Toolbox.roundScore(0.44681886), 0.447);
        Assertions.assertEquals(Double.parseDouble("16- Score not round properly"), Toolbox.roundScore(0.44962552), 0.45); // ask
        Assertions.assertEquals(Double.parseDouble("17- Score not round properly"), Toolbox.roundScore(0.9921667), 0.992);
        Assertions.assertEquals(Double.parseDouble("18- Score not round properly"), Toolbox.roundScore(0.9919758), 0.992);
        Assertions.assertEquals(Double.parseDouble("19- Score not round properly"), Toolbox.roundScore(0.98239964), 0.982);
        Assertions.assertEquals(Double.parseDouble("20- Score not round properly"), Toolbox.roundScore(0.999), 0.999);

        Assertions.assertEquals(Double.parseDouble("21- Score not round properly"), Toolbox.roundScore(0.9999), 1.0); // ask
    }

    @Test
    public void testGetInteractionsAndRemoveDuplicates() throws SQLException, InvalidInteractionResourceException {
        List<Interaction> interactions = interactionService.getInteractions(ACCESSION, InteractorConstant.STATIC);

        Assertions.assertFalse(interactions.isEmpty(), "Interactors list is Empty");
        Assertions.assertTrue(interactions.size() > 10, "Interactor less than 10");

    }
}
