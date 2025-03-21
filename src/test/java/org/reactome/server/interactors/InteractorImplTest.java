package org.reactome.server.interactors;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactome.server.interactors.dao.InteractorDAO;
import org.reactome.server.interactors.dao.intact.StaticInteractor;
import org.reactome.server.interactors.database.InteractorsDatabase;
import org.reactome.server.interactors.model.Interactor;
import org.reactome.server.interactors.util.Toolbox;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorImplTest extends BaseImplTest{

    private InteractorDAO interactorDAO;

    @BeforeEach
    public void setUp() throws IOException {
        Properties prop = new Properties();
        InteractorsDatabase interactors = null;
        try {//src/test/resources/
            InputStream is = InteractionImplTest.class.getResourceAsStream("/db.properties");
            prop.load(is);
            interactors = new InteractorsDatabase(prop.getProperty("database"));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        interactorDAO = new StaticInteractor(interactors);
    }

    @Test
    public void testSimpleQuery() throws SQLException {
        List<Interactor> allInteractors = interactorDAO.getAll();

        Assertions.assertTrue(allInteractors.size() > 1, "No interactor present in the database.");

    }

    @Test
    public void testUniprotAccessionRegex() {
        Assertions.assertTrue(Toolbox.isUniprotAccession("A2BC19"), "Not a valid Uniprot accession.");
        Assertions.assertTrue(Toolbox.isUniprotAccession("P12345"), "Not a valid Uniprot accession.");
        Assertions.assertTrue(Toolbox.isUniprotAccession("A0A022YWF9"), "Not a valid Uniprot accession.");
        Assertions.assertTrue(Toolbox.isUniprotAccession("Q9WMX2-PRO_0000037552"), "Not a valid Uniprot accession.");
        Assertions.assertTrue(Toolbox.isUniprotAccession("Q9WMX2-2"), "Not a valid Uniprot accession.");
        Assertions.assertFalse(Toolbox.isUniprotAccession("XX1323"), "Accession XX1323 is not a valid one.");
        Assertions.assertFalse(Toolbox.isUniprotAccession("123445"), "Accession 123445 is not a valid one.");
    }
}
