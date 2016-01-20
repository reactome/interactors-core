package org.reactome.server.tools.interactors.dao.psicquic;

import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.model.Interactor;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorImpl implements InteractorDAO {
    @Override
    public Interactor getByAccession(String acc) throws SQLException {
        return null;
    }

    @Override
    public void searchByAccessions(Interactor interactorA, Interactor interactorB) throws SQLException {

    }

    @Override
    public boolean exists(String acc) throws SQLException {
        return false;
    }

    @Override
    public List<String> getAllAccessions() throws SQLException {
        return null;
    }

    @Override
    public Interactor create(Interactor interactor) throws SQLException {
        return null;
    }

    @Override
    public boolean update(Interactor interactor) throws SQLException {
        return false;
    }

    @Override
    public Interactor getById(String id) throws SQLException {
        return null;
    }

    @Override
    public List<Interactor> getAll() throws SQLException {
        return null;
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return false;
    }
}
