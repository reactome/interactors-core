package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.dao.impl.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class DAOFactory {

    public static InteractorDAO createInteractorDAO(){
        return new JDBCInteractorImpl();
    }

    public static InteractionDAO createInterationDAO() {
        return new JDBCInteractionImpl();
    }

    public static InteractionDetailsDAO createInterationDetailsDAO() {
        return new JDBCInteractionDetailsImpl();
    }

    public static InteractionResourceDAO createInterationResourceDAO() {
        return new JDBCInteractionResourceImpl();
    }

    public static InteractorResourceDAO createInteratorResourceDAO() { return new JDBCInteractorResourceImpl(); }

}
