package org.reactome.server.tools.interactors.psicquic.clients;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MatrixdbClient extends AbstractClient {

    MatrixdbClient(String resource) {
        super(resource);
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,ebi,matrixdb";
    }
}
