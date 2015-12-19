package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.dao.impl.JDBCInteractionDetailsImpl;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractionImpl;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;

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

}
