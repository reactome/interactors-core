package org.reactome.server.tool.interactors.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactome.server.interactors.dao.InteractorDAO;
import org.reactome.server.interactors.dao.intact.StaticInteractor;
import org.reactome.server.interactors.database.InteractorsDatabase;
import org.reactome.server.interactors.model.Interactor;
import org.reactome.server.interactors.util.Toolbox;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractorImpl {

    private InteractorDAO interactorDAO;

    @Before
    public void setUp() {
        String file = "/Users/reactome/interactors/interactors.db";
        InteractorsDatabase interactors = null;
        try {
            interactors = new InteractorsDatabase(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        interactorDAO = new StaticInteractor(interactors);
    }

    @Test
    public void testSimpleQuery() throws SQLException {
        List<Interactor> allInteractors = interactorDAO.getAll();

        Assert.assertTrue("No interactor present in the database.", allInteractors.size() > 1);

    }

    @Test
    public void testUniprotAccessionRegex(){
        Assert.assertTrue("Not a valid Uniprot accession.", Toolbox.isUniprotAccession("A2BC19"));
        Assert.assertTrue("Not a valid Uniprot accession.", Toolbox.isUniprotAccession("P12345"));
        Assert.assertTrue("Not a valid Uniprot accession.", Toolbox.isUniprotAccession("A0A022YWF9"));
        Assert.assertTrue("Not a valid Uniprot accession.", Toolbox.isUniprotAccession("Q9WMX2-PRO_0000037552"));
        Assert.assertTrue("Not a valid Uniprot accession.", Toolbox.isUniprotAccession("Q9WMX2-2"));
        Assert.assertFalse("Accession XX1323 is not a valid one.", Toolbox.isUniprotAccession("XX1323"));
        Assert.assertFalse("Accession 123445 is not a valid one.", Toolbox.isUniprotAccession("123445"));
    }
}
