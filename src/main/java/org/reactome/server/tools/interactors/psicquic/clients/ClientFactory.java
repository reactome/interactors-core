package org.reactome.server.tools.interactors.psicquic.clients;


import org.reactome.server.tools.interactors.psicquic.PsicquicClient;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class ClientFactory {

    public static PsicquicClient getClient(String resource) {
        switch (resource.toUpperCase()) {
            /** ChEMBL won't query its own client. See comments in ChemblClient. **/
//            case "CHEMBL":
//                return new ChemblClient(resource);
            case "BIND":
                return new BindClient(resource);
            case "BIOGRID":
                return new BiogridClient(resource);
            case "MINT":
                return new MintClient(resource);
            case "MENTHA":
                return new MenthaClient(resource);
            case "MATRIXDB":
                return new MatrixdbClient(resource);
            case "INNATEDB":
            case "INNATEDB-ALL":
                return new InnatedbClient(resource);
            case "REACTOME-FIS":
                return new ReactomeFisClient(resource);
            default:
                return new GenericClient(resource);
        }
    }
}
