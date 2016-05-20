package org.reactome.server.interactors.psicquic.clients;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GeneManiaClient extends AbstractClient {

    GeneManiaClient(String resource) {
        super(resource);
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,ensembl,irefindex,ddbj/embl/genbank,refseq,unknown";
    }
}
