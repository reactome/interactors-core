package org.reactome.server.tool.interactors.dao;

import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.reactome.server.tools.interactors.model.Interactor;
import org.reactome.server.tools.interactors.model.InteractorResource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractorImpl {

    private InteractorDAO interactorDAO;

    @Before
    public void setUp() {
        interactorDAO = DAOFactory.createInteractorDAO();
    }

    @Test
    public void testSimpleQuery() throws SQLException {
        interactorDAO.getAll();
    }

}
