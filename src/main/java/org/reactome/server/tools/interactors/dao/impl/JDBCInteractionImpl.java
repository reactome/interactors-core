package org.reactome.server.tools.interactors.dao.impl;

import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.Interactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class JDBCInteractionImpl implements InteractionDAO {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private Connection connection;

    private final String TABLE = "INTERACTION";
    private final String ALL_COLUMNS = "INTERACTOR_A, INTERACTOR_B, AUTHOR_SCORE, MISCORE, INTERACTION_RESOURCE_ID";
    private final String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public JDBCInteractionImpl(InteractorsDatabase database) {
        this.connection = database.getConnection();
    }

    public Interaction create(Interaction interaction) throws SQLException {
        try {
            String query = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?, ?, ?, ?)";

            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setLong(1, interaction.getInteractorA().getId());
            pstm.setLong(2, interaction.getInteractorB().getId());
            pstm.setDouble(3, interaction.getAuthorScore());
            pstm.setDouble(4, interaction.getIntactScore());
            pstm.setLong(5, interaction.getInteractionResourceId());

            if (pstm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        interaction.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating Interaction failed, no ID obtained.");
                    }
                }
            }
        } finally {
            //conn.close();
        }

        return interaction;
    }

    /**
     * Create interactions using same transaction.
     *
     * @param interactions
     * @return
     * @throws SQLException
     */
    public boolean create(List<Interaction> interactions) throws SQLException {
        connection.setAutoCommit(false);

        try {
            String query = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?, ?, ?, ?)";

            PreparedStatement pstm = connection.prepareStatement(query);

            for (Interaction interaction : interactions) {
                pstm.setLong(1, interaction.getInteractorA().getId());
                pstm.setLong(2, interaction.getInteractorB().getId());
                pstm.setDouble(3, interaction.getAuthorScore());
                pstm.setDouble(4, interaction.getIntactScore());
                pstm.setLong(5, interaction.getInteractionResourceId());

                //pstm.addBatch();
                if (pstm.executeUpdate() > 0) {
                    try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            interaction.setId(generatedKeys.getLong(1));
                        } else {
                            throw new SQLException("Creating Interaction failed, no ID obtained.");
                        }
                    }
                }


                // maybe create the interaction details here, but it is not a good practice have two DAO's talking
                // each other.

            }

            connection.commit();
        }catch (SQLException e){
            logger.error("An error has occurred during interaction batch insert. Please check the following exception.");
            connection.rollback();

            throw new SQLException(e);

        } finally {
            connection.setAutoCommit(true);
            //conn.close();
        }

        return true;
    }

    public boolean update(Interaction interaction) throws SQLException {
        return false;
    }

    public Interaction getById(String id) throws SQLException {
        return null;
    }

    public List<Interaction> getAll() throws SQLException {
        return null;
    }

    public boolean delete(String id) throws SQLException {
        return false;
    }

    public List<Interaction> getByAcc(String acc, Long resourceId, Integer page, Integer pageSize) throws SQLException{
        List<String> accList = new ArrayList<>(1);
        accList.add(acc);

        return getByAcc(accList, resourceId, page, pageSize);
    }

    public List<Interaction> getByAcc(List<String> accs, Long resourceId, Integer page, Integer pageSize) throws SQLException{
        List<Interaction> interactions = new ArrayList<>();

        try {
            String query = "SELECT   INTERACTION.ID AS 'INTERACTION_ID', " +
                                    "INTERACTORA.ID AS 'ID_A', INTERACTORA.ACC AS 'ACC_A', INTERACTORA.ALIAS AS 'ALIAS_A', INTERACTORA.INTERACTOR_RESOURCE_ID AS 'INTERACTOR_RESOURCE_A_ID', INTERACTORA.INTACT_ID AS 'INTACT_IDA', INTERACTORA.TAXID AS 'TAX_IDA', " +
                                    "INTERACTORB.ID AS 'ID_B', INTERACTORB.ACC AS 'ACC_B', INTERACTORB.ALIAS AS 'ALIAS_B', INTERACTORB.INTERACTOR_RESOURCE_ID AS 'INTERACTOR_RESOURCE_B_ID', INTERACTORB.INTACT_ID AS 'INTACT_IDB', INTERACTORB.TAXID AS 'TAX_IDB', " +
                                    "INTERACTION.AUTHOR_SCORE, " +
                                    "INTERACTION.MISCORE, " +
                                    "INTERACTION.INTERACTION_RESOURCE_ID " +
                           "FROM     INTERACTION, INTERACTOR AS INTERACTORA, INTERACTOR AS INTERACTORB " +
                           "WHERE    INTERACTORA.ID = INTERACTION.INTERACTOR_A " +
                           "AND      INTERACTORB.ID = INTERACTION.INTERACTOR_B " +
                           "AND      (INTERACTION.INTERACTOR_A = (select id from interactor where ACC = ?) OR INTERACTION.INTERACTOR_B = (select id from interactor where ACC = ?)) " +
                           "AND      INTERACTION.INTERACTION_RESOURCE_ID = ? " +
                           "ORDER BY INTERACTION.MISCORE DESC";

            /** Both are greater than -1, paginated is enabled **/
            if(page > -1 && pageSize > -1){
                int limit = (pageSize * page) - pageSize;
                String limitQuery = String.format(" LIMIT %d, %d", limit, pageSize);

                query = query.concat(limitQuery);
            }

            for (String acc : accs) {
                PreparedStatement pstm = connection.prepareStatement(query);
                pstm.setString(1, acc.toUpperCase());
                pstm.setString(2, acc.toUpperCase());
                pstm.setLong(3, resourceId);

                ResultSet rs = pstm.executeQuery();
                while(rs.next()){
                    Interaction interaction = buildInteractionByAccession(acc, rs);

                    interactions.add(interaction);
                }

            }

        }catch (SQLException e){
            logger.error("An error has occurred during interaction batch insert. Please check the following exception.");
            throw new SQLException(e);

        } finally {
            //conn.close();
        }

        return interactions;
    }

    public List<Interaction> getByIntactId(String intactId, Long resourceId, Integer page, Integer pageSize) throws SQLException{
        List<String> intactIdList = new ArrayList<>(1);
        intactIdList.add(intactId);

        return getByIntactId(intactIdList, resourceId, page, pageSize);
    }

    public List<Interaction> getByIntactId(List<String> intactIdList, Long resourceId, Integer page, Integer pageSize) throws SQLException{
        List<Interaction> interactions = new ArrayList<>();

        try {
            String query = "SELECT   INTERACTION.ID AS 'INTERACTION_ID', " +
                    "INTERACTORA.ID AS 'ID_A', INTERACTORA.ACC AS 'ACC_A', INTERACTORA.ALIAS AS 'ALIAS_A', INTERACTORA.INTERACTOR_RESOURCE_ID AS 'INTERACTOR_RESOURCE_A_ID', INTERACTORA.INTACT_ID AS 'INTACT_IDA',  INTERACTORA.TAXID AS 'TAX_IDA', " +
                    "INTERACTORB.ID AS 'ID_B', INTERACTORB.ACC AS 'ACC_B', INTERACTORB.ALIAS AS 'ALIAS_B', INTERACTORB.INTERACTOR_RESOURCE_ID AS 'INTERACTOR_RESOURCE_B_ID', INTERACTORB.INTACT_ID AS 'INTACT_IDB',  INTERACTORB.TAXID AS 'TAX_IDB', " +
                    "INTERACTION.AUTHOR_SCORE, " +
                    "INTERACTION.MISCORE, " +
                    "INTERACTION.INTERACTION_RESOURCE_ID " +
                    "FROM     INTERACTION, INTERACTOR AS INTERACTORA, INTERACTOR AS INTERACTORB " +
                    "WHERE    INTERACTORA.ID = INTERACTION.INTERACTOR_A " +
                    "AND      INTERACTORB.ID = INTERACTION.INTERACTOR_B " +
                    "AND      (INTERACTION.INTERACTOR_A = (select id from interactor where INTACT_ID = ?) OR INTERACTION.INTERACTOR_B = (select id from interactor where INTACT_ID = ?)) " +
                    "AND      INTERACTION.INTERACTION_RESOURCE_ID = ? " +
                    "ORDER BY INTERACTION.MISCORE DESC";

            /** Both are greater than -1, paginated is enabled **/
            if(page > -1 && pageSize > -1){
                int limit = (pageSize * page) - pageSize;
                String limitQuery = String.format(" LIMIT %d, %d", limit, pageSize);

                query = query.concat(limitQuery);
            }

            for (String intactId : intactIdList) {
                PreparedStatement pstm = connection.prepareStatement(query);
                pstm.setString(1, intactId.toUpperCase());
                pstm.setString(2, intactId.toUpperCase());
                pstm.setLong(3, resourceId);

                ResultSet rs = pstm.executeQuery();
                while(rs.next()){
                    Interaction interaction = buildInteractionByIntactId(intactId, rs);

                    interactions.add(interaction);
                }

            }

        }catch (SQLException e){
            logger.error("An error has occurred during interaction batch insert. Please check the following exception.");
            throw new SQLException(e);

        } finally {
            //conn.close();
        }

        return interactions;
    }

    @Override
    public Map<String, Integer> countByAccesssions(Collection<String> accs, Long resourceId) throws SQLException {
        Map<String, Integer> interactionsCountMap = new HashMap<>();

        try {
            String csvValues = "";
            for (String acc : accs) {
                csvValues = csvValues.concat("'").concat(acc).concat("'").concat(",");
            }
            csvValues = csvValues.substring(0, csvValues.length()-1);

            String query = "SELECT   accession, SUM(count_) as total_ " +
                            "FROM (" +
                                "SELECT   COUNT(*) AS count_, INTERACTORA.acc AS accession " +
                                "FROM     INTERACTION, INTERACTOR AS INTERACTORA " +
                                "WHERE    INTERACTORA.ID = INTERACTION.INTERACTOR_A " +
                                "AND      INTERACTION.INTERACTOR_A IN (select id from interactor where ACC in (" + csvValues.toUpperCase() + ")) " +
                                "AND      INTERACTION.INTERACTION_RESOURCE_ID = ? " +
                                "GROUP BY INTERACTORA.acc " +
                                "UNION ALL " +
                                "SELECT   COUNT(*) AS count_, INTERACTORB.acc AS accession " +
                                "FROM     INTERACTION, INTERACTOR AS INTERACTORB " +
                                "WHERE    INTERACTORB.ID = INTERACTION.INTERACTOR_B " +
                                "AND      INTERACTION.INTERACTOR_B IN (select id from interactor where ACC in (" + csvValues.toUpperCase() + ")) " +
                                "AND      INTERACTION.INTERACTOR_A <> INTERACTION.INTERACTOR_B " +
                                "AND      INTERACTION.INTERACTION_RESOURCE_ID = ? " +
                                "GROUP BY INTERACTORB.acc " +
                            ")" +
                            "GROUP BY accession";

            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setLong(1, resourceId);
            pstm.setLong(2, resourceId);

            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                interactionsCountMap.put(rs.getString("accession"), rs.getInt("total_"));
            }

        }catch (SQLException e){
            logger.error("An error has occurred during interaction batch insert. Please check the following exception.");
            throw new SQLException(e);

        } finally {
            //conn.close();
        }

        return interactionsCountMap;
    }

    /**
     * Helper method for creating Interaction object
     * To reuse it make sure you are using the same alias in your query when projecting columns
     * e.g  select INTERACTOR.ID AS 'ID_A', and so on.
     *
     * @param acc
     * @param rs
     * @return
     * @throws SQLException
     */
    private Interaction buildInteractionByAccession(String acc, ResultSet rs) throws SQLException {
        Interaction interaction = new Interaction();
        interaction.setId(rs.getLong("INTERACTION_ID"));

        Interactor interactorA = new Interactor();
        interactorA.setId(rs.getLong("ID_A"));
        interactorA.setAcc(rs.getString("ACC_A"));
        interactorA.setAlias(rs.getString("ALIAS_A"));
        interactorA.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_A_ID"));
        interactorA.setIntactId(rs.getString("INTACT_IDA"));
        interactorA.setTaxid(rs.getInt("TAX_IDA"));

        Interactor interactorB = new Interactor();
        interactorB.setId(rs.getLong("ID_B"));
        interactorB.setAcc(rs.getString("ACC_B"));
        interactorB.setAlias(rs.getString("ALIAS_B"));
        interactorB.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_B_ID"));
        interactorB.setIntactId(rs.getString("INTACT_IDB"));
        interactorB.setTaxid(rs.getInt("TAX_IDB"));

        /**
         * If A interacts with B and B with A we are talking about the same interaction, so
         * just to keep it easy to create the JSON - the interactor in the query will be always on side of A
         * otherwise just set them as A.set(b) and B.set(a).
         */
        if(acc.equals(interactorA.getAcc())) {
            interaction.setInteractorA(interactorA);
            interaction.setInteractorB(interactorB);
        }else {
            interaction.setInteractorA(interactorB);
            interaction.setInteractorB(interactorA);
        }

        interaction.setAuthorScore(rs.getDouble("AUTHOR_SCORE"));
        interaction.setIntactScore(rs.getDouble("MISCORE"));
        interaction.setInteractionResourceId(rs.getLong("INTERACTION_RESOURCE_ID"));

        return interaction;
    }

    /**
     * Helper method for creating Interaction object
     * To reuse it make sure you are using the same alias in your query when projecting columns
     * e.g  select INTERACTOR.ID AS 'ID_A', and so on.
     *
     * @param intactId
     * @param rs
     * @return
     * @throws SQLException
     */
    private Interaction buildInteractionByIntactId(String intactId, ResultSet rs) throws SQLException {
        Interaction interaction = new Interaction();
        interaction.setId(rs.getLong("INTERACTION_ID"));

        Interactor interactorA = new Interactor();
        interactorA.setId(rs.getLong("ID_A"));
        interactorA.setAcc(rs.getString("ACC_A"));
        interactorA.setAlias(rs.getString("ALIAS_A"));
        interactorA.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_A_ID"));
        interactorA.setIntactId(rs.getString("INTACT_IDA"));
        interactorA.setTaxid(rs.getInt("TAX_IDA"));

        Interactor interactorB = new Interactor();
        interactorB.setId(rs.getLong("ID_B"));
        interactorB.setAcc(rs.getString("ACC_B"));
        interactorB.setAlias(rs.getString("ALIAS_B"));
        interactorB.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_B_ID"));
        interactorB.setIntactId(rs.getString("INTACT_IDB"));
        interactorB.setTaxid(rs.getInt("TAX_IDB"));

        /**
         * If A interacts with B and B with A we are talking about the same interaction, so
         * just to keep it easy to create the JSON - the interactor in the query will be always on side of A
         * otherwise just set them as A.set(b) and B.set(a).
         */
        if(intactId.equals(interactorA.getIntactId())) {
            interaction.setInteractorA(interactorA);
            interaction.setInteractorB(interactorB);
        }else {
            interaction.setInteractorA(interactorB);
            interaction.setInteractorB(interactorA);
        }

        interaction.setAuthorScore(rs.getDouble("AUTHOR_SCORE"));
        interaction.setIntactScore(rs.getDouble("MISCORE"));
        interaction.setInteractionResourceId(rs.getLong("INTERACTION_RESOURCE_ID"));

        return interaction;
    }
}
