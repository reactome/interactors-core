package org.reactome.server.tools.interactors.psicquic.clients;


import org.reactome.server.tools.interactors.psicquic.PsicquicClient;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class ClientFactory {

    public static PsicquicClient getClient(String resource) {
        switch (resource.toUpperCase()) {
            case "CHEMBL":
                return new ChemblClient(resource);
            default:
                return new GenericClient(resource);
        }
    }
}
