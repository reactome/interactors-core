package org.reactome.server.tools.interactors.dao.impl;

import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.database.SQLiteConnection;
import org.reactome.server.tools.interactors.model.Interactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class JDBCInteractorImpl implements InteractorDAO {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private SQLiteConnection database = SQLiteConnection.getInstance();

    private String TABLE = "INTERACTOR";
    private String ALL_COLUMNS = "ACC, INTACT_ID, INTERACTOR_RESOURCE_ID";
    private String ALL_COLUMNS_SEL = "ID, CREATE_DATE, ".concat(ALL_COLUMNS);

    public JDBCInteractorImpl() {

    }

    public Interactor create(Interactor interactor) throws SQLException {
        Connection conn = database.getConnection();

        try {
            String insert = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?, ?)";

            PreparedStatement pstm = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, interactor.getAcc());
            pstm.setString(2, interactor.getIntactId());
            pstm.setLong(3, interactor.getInteractorResourceId());

            if(pstm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        interactor.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating Interactor failed, no ID obtained.");
                    }
                }
            }

        } finally {
            //conn.close();
        }

        return interactor;

    }

    public boolean update(Interactor interactor) {
        return false;
    }

    public Interactor getById(String id) throws SQLException {
        Interactor ret = null;

        String query = "SELECT " + ALL_COLUMNS_SEL +
                " FROM " + TABLE +
                " WHERE ID = ?";

        Connection conn = database.getConnection();

        try {
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, id);

            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                ret = buildInteractor(rs);
            }
        } finally {
            //conn.close();
        }

        return ret;
    }

    public Interactor getByAccession(String acc) throws SQLException {
        Interactor ret = null;

        String query = "SELECT " + ALL_COLUMNS_SEL +
                " FROM " + TABLE +
                " WHERE ACC = ?";

        Connection conn = database.getConnection();

        try {
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, acc);

            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                ret = buildInteractor(rs);
            }
        } finally {
            //conn.close();
        }

        return ret;
    }

    public boolean exists(String acc) throws SQLException {
        Interactor i = getByAccession(acc);
        return (i != null);
    }

    public boolean delete(String id) throws SQLException {
        Connection conn = database.getConnection();

        try {
            String query = "DELETE FROM " + TABLE + " WHERE ID = ?";

            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, id);

            if (pstm.executeUpdate() > 0) {
                return true;
            }

        } finally {
            conn.close();
        }

        return false;
    }

    public List<Interactor> getAll() throws SQLException {
        List<Interactor> ret = new ArrayList<Interactor>();

        String query = "SELECT " + ALL_COLUMNS_SEL +
                " FROM " + TABLE;

        Connection conn = database.getConnection();

        try {
            PreparedStatement pstm = conn.prepareStatement(query);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Interactor interactor = buildInteractor(rs);
                ret.add(interactor);
            }

        } finally {
            //conn.close();
        }

        return ret;
    }

    private Interactor buildInteractor(ResultSet rs) throws SQLException {
        Interactor ret = new Interactor();
        ret.setId(rs.getLong("ID"));
        ret.setAcc(rs.getString("ACC"));
        ret.setIntactId(rs.getString("INTACT_ID"));
        ret.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_ID"));
        //ret.setCreateDate(rs.getTimestamp("CREATE_DATE"));

        return ret;
    }

}
