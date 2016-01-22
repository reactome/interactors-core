package org.reactome.server.tool.interactors.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.dao.intact.StaticInteractor;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.Interactor;

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

}
