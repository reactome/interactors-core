package org.reactome.server.tools.interactors.dao.intact;

import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.Interactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class StaticInteractor implements InteractorDAO {

    final Logger logger = LoggerFactory.getLogger(StaticInteractor.class);

    private Connection connection;

    private String TABLE = "INTERACTOR";
    private String ALL_COLUMNS = "ACC, INTACT_ID, INTERACTOR_RESOURCE_ID, ALIAS, TAXID";
    private String ALL_COLUMNS_SEL = "ID, CREATE_DATE, ".concat(ALL_COLUMNS);

    public StaticInteractor(InteractorsDatabase database) {
        this.connection = database.getConnection();
    }

    public Interactor create(Interactor interactor) throws SQLException {
        try {
            String insert = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?, ?, ?, ?)";

            PreparedStatement pstm = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, interactor.getAcc());
            pstm.setString(2, interactor.getIntactId());
            pstm.setLong(3, interactor.getInteractorResourceId());
            pstm.setString(4, interactor.getAlias());
            Integer taxId = interactor.getTaxid();
            if (taxId == null) {
                logger.error("TaxId is null for " + interactor.toString());
                taxId = -1;
            }
            pstm.setInt(5, taxId);


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
            //connection.close();
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

        try {
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setString(1, id);

            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                ret = buildInteractor(rs);
            }
        } finally {
            //connection.close();
        }

        return ret;
    }

    public Interactor getByAccession(String acc) throws SQLException {
        Interactor ret = null;

        String query = "SELECT " + ALL_COLUMNS_SEL +
                " FROM " + TABLE +
                " WHERE ACC = ?";

        try {
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setString(1, acc);

            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                ret = buildInteractor(rs);
            }
        } finally {
            //connection.close();
        }

        return ret;
    }

    /**
     * Querying for InteractorA and InteractorB in the same database call.
     * Attributes will be set via object reference.
     * @param interactorA link a
     * @param interactorB link b
     * @throws SQLException
     */
    public void searchByAccessions(Interactor interactorA, Interactor interactorB) throws SQLException {

        String query = "SELECT " + ALL_COLUMNS_SEL +
                        " FROM " + TABLE +
                        " WHERE ACC IN (?, ?)";

        try {
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setString(1, interactorA.getAcc());
            pstm.setString(2, interactorB.getAcc());

            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                /**
                 * Can't predict the query result order in the IN-clause, it means, can't figure out
                 * which is A or B, so the if clause is checking that and setting the object by reference.
                 * If interactor ID remains null, then it does not exist in the database.
                 */
                Interactor tempInteractor = buildInteractor(rs);

                if(interactorA.getAcc().equals(interactorB.getAcc())){
                    interactorA.setId(tempInteractor.getId());
                    interactorB.setId(tempInteractor.getId());
                }else {
                    // is A
                    if (tempInteractor.getAcc().equals(interactorA.getAcc())) {
                        interactorA.setId(tempInteractor.getId());
                    }

                    // is B
                    if (tempInteractor.getAcc().equals(interactorB.getAcc())) {
                        interactorB.setId(tempInteractor.getId());
                    }
                }

            }

        } finally {
            //connection.close();
        }

    }

    public boolean exists(String acc) throws SQLException {
        Interactor i = getByAccession(acc);
        return (i != null);
    }

    public boolean delete(String id) throws SQLException {
        try {
            String query = "DELETE FROM " + TABLE + " WHERE ID = ?";

            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setString(1, id);

            if (pstm.executeUpdate() > 0) {
                return true;
            }

        } finally {
            connection.close();
        }

        return false;
    }

    public List<Interactor> getAll() throws SQLException {
        List<Interactor> ret = new ArrayList<Interactor>();

        String query = "SELECT " + ALL_COLUMNS_SEL +
                " FROM " + TABLE;

        try {
            PreparedStatement pstm = connection.prepareStatement(query);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Interactor interactor = buildInteractor(rs);
                ret.add(interactor);
            }

        } finally {
            //connection.close();
        }

        return ret;
    }

    private Interactor buildInteractor(ResultSet rs) throws SQLException {
        Interactor ret = new Interactor();
        ret.setId(rs.getLong("ID"));
        ret.setAcc(rs.getString("ACC"));
        ret.setIntactId(rs.getString("INTACT_ID"));
        ret.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_ID"));
        ret.setAlias(rs.getString("ALIAS"));
        //ret.setCreateDate(rs.getTimestamp("CREATE_DATE"));
        ret.setTaxid(rs.getInt("TAXID"));

        return ret;
    }

    public List<String> getAllAccessions() throws SQLException {
        List<String> ret = new ArrayList<>();

        String query = "SELECT ACC FROM " + TABLE;

        try {
            PreparedStatement pstm = connection.prepareStatement(query);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                ret.add(rs.getString("ACC"));
            }
        } finally {
            //connection.close();
        }

        return ret;
    }
}
