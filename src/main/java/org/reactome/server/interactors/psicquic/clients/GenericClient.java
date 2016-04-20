package org.reactome.server.interactors.psicquic.clients;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class GenericClient extends AbstractClient {

    GenericClient(String resource) {
        super(resource);
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,intact,irefindex,ddbj/embl/genbank,refseq,unknown";
    }
}
