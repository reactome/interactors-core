package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.Interactor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorService {

    //final Logger logger = LoggerFactory.getLogger(InteractionService.class);

    private InteractorDAO interactorDAO;

    public InteractorService(InteractorsDatabase database){
        this.interactorDAO = new JDBCInteractorImpl(database);
    }

    public List<Interactor> getAll() throws SQLException {
        return interactorDAO.getAll();
    }

    public List<String> getAllAccessions() throws SQLException {
        return interactorDAO.getAllAccessions();
    }


}
