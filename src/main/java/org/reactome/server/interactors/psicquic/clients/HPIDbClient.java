package org.reactome.server.interactors.psicquic.clients;

import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class HPIDbClient extends AbstractClient {

    HPIDbClient(String resource) {
        super(resource);
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,intact,irefindex,ddbj/embl/genbank,refseq,unknown";
    }

    /**
     * Retrieve all the alias of a given accession
     *
     * @param accessions key=dbsource{psi-mi,uniprot,etc} value=alias
     */
    public String getAlias(Map<String, List<String>> accessions) {
        String psimiAlias = "";
        String uniprotAlias = "";
        String otherAlias = "";

        for (String dbSource : accessions.keySet()) {
            if (dbSource.equalsIgnoreCase("psi-mi") && psimiAlias.isEmpty()) {
                psimiAlias = accessions.get(dbSource).get(0);
            } else if (dbSource.equalsIgnoreCase("uniprotkb") && uniprotAlias.isEmpty()) {
                uniprotAlias = accessions.get(dbSource).get(0);
            } else if (otherAlias.isEmpty()) {
                otherAlias = accessions.get(dbSource).get(0);
            }
        }

        String rtn = otherAlias;
        if (!psimiAlias.isEmpty()){
            rtn = psimiAlias;
        } else if (!uniprotAlias.isEmpty()){
            rtn = uniprotAlias;
        }

        return rtn.isEmpty() ? null : rtn.toUpperCase();
    }
}
