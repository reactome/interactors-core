package org.reactome.server.interactors.psicquic.clients;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MintClient extends AbstractClient {

    MintClient(String resource) {
        super(resource);
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,intact,mint";
    }
}
