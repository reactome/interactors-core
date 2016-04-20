package org.reactome.server.interactors.service;

import org.reactome.server.interactors.dao.InteractorDAO;
import org.reactome.server.interactors.dao.intact.StaticInteractor;
import org.reactome.server.interactors.database.InteractorsDatabase;
import org.reactome.server.interactors.model.Interactor;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorService {

    private InteractorDAO interactorDAO;

    public InteractorService(InteractorsDatabase database){
        this.interactorDAO = new StaticInteractor(database);
    }

    public List<Interactor> getAll() throws SQLException {
        return interactorDAO.getAll();
    }

    public List<String> getAllAccessions() throws SQLException {
        return interactorDAO.getAllAccessions();
    }


}
