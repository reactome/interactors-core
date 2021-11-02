package org.reactome.server.tool.interactors;


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
public class InteractionImplTest {

    private InteractionDAO interactionDAO;

    private InteractionService interactionService;

    private final String ACCESSION = "UniProt:Q13501";
    private final Long RESOURCE_ID = 1L;

    @BeforeEach
    public void setUp() throws IOException {
        Properties prop = new Properties();
        InteractorsDatabase interactors = null;
        try {
            InputStream is = InteractionImplTest.class.getResourceAsStream("/db.properties");
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
        Assertions.assertEquals(0.468, Toolbox.roundScore(0.467866), "1- Score not round properly");
        Assertions.assertEquals(0.457, Toolbox.roundScore(0.457123), "2- Score not round properly");
        Assertions.assertEquals(0.444, Toolbox.roundScore(0.444444), "3- Score not round properly");
        Assertions.assertEquals(1.556, Toolbox.roundScore(1.5555), "4- Score not round properly");
        Assertions.assertEquals(0.456, Toolbox.roundScore(0.45555), "5- Score not round properly");
        Assertions.assertEquals(0.449, Toolbox.roundScore(0.4490), "6- Score not round properly");
        Assertions.assertEquals(0.45, Toolbox.roundScore(0.4499), "7- Score not round properly");
        Assertions.assertEquals(0.45, Toolbox.roundScore(0.45), "8- Score not round properly");
        Assertions.assertEquals(0.45, Toolbox.roundScore(0.4499999), "8- Score not round properly");
        Assertions.assertEquals(0.445, Toolbox.roundScore(0.4445), "9- Score not round properly");
        Assertions.assertEquals(0.447, Toolbox.roundScore(0.4466), "10- Score not round properly");
        Assertions.assertEquals(0.447, Toolbox.roundScore(0.447), "11- Score not round properly");
        Assertions.assertEquals(0.443, Toolbox.roundScore(0.44295776), "12- Score not round properly");
        Assertions.assertEquals(0.445, Toolbox.roundScore(0.44547057), "13- Score not round properly");
        Assertions.assertEquals(0.446, Toolbox.roundScore(0.44611502), "14- Score not round properly");
        Assertions.assertEquals(0.447, Toolbox.roundScore(0.44681886), "15- Score not round properly");
        Assertions.assertEquals(0.45, Toolbox.roundScore(0.44962552), "16- Score not round properly");
        Assertions.assertEquals(0.992, Toolbox.roundScore(0.9921667), "17- Score not round properly");
        Assertions.assertEquals(0.992, Toolbox.roundScore(0.9919758), "18- Score not round properly");
        Assertions.assertEquals(0.982, Toolbox.roundScore(0.98239964), "19- Score not round properly");
        Assertions.assertEquals(0.999, Toolbox.roundScore(0.999), "20- Score not round properly");
        Assertions.assertEquals(1.0, Toolbox.roundScore(0.9999),"21- Score not round properly");
    }

    @Test
    public void testGetInteractionsAndRemoveDuplicates() throws SQLException, InvalidInteractionResourceException {
        List<Interaction> interactions = interactionService.getInteractions(ACCESSION, InteractorConstant.STATIC);

        Assertions.assertFalse(interactions.isEmpty(), "Interactors list is Empty");
        Assertions.assertTrue(interactions.size() > 10, "Interactor less than 10");

    }
}
