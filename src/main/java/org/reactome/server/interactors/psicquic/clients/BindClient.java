package org.reactome.server.interactors.psicquic.clients;

import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class BindClient extends AbstractClient {

    BindClient(String resource) {
        super(resource);
    }

    @Override
    public String getAlias(Map<String, List<String>> accessions) {
        String psimiAlias = "";
        String uniprotAlias = "";
        String bindAlias = "";

        for (String dbSource : accessions.keySet()) {
            if (dbSource.equalsIgnoreCase("psi-mi") && psimiAlias.isEmpty()) {
                psimiAlias = accessions.get(dbSource).get(0);
            } else if (dbSource.equalsIgnoreCase("uniprotkb") && uniprotAlias.isEmpty()) {
                uniprotAlias = accessions.get(dbSource).get(0);
            } else if ((dbSource.equalsIgnoreCase("ecocyc") || dbSource.equalsIgnoreCase("unknown")) && bindAlias.isEmpty()) {
                bindAlias = accessions.get(dbSource).get(0);
            }
        }

        //This is only reached in case there is no "unknown" key in the response
        String rtn = psimiAlias;
        if (!uniprotAlias.isEmpty()) {
            rtn = uniprotAlias;
        }else if(!bindAlias.isEmpty()){
            rtn = bindAlias;
        }

        return rtn.isEmpty() ? null : rtn.toUpperCase();
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,irefindex,ddbj/embl/genbank,refseq,unknown";
    }
}
