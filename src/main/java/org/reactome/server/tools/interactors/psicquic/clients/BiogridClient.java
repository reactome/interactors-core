package org.reactome.server.tools.interactors.psicquic.clients;

import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class BiogridClient extends AbstractClient {

    //"http://www.ncbi.nlm.nih.gov/gene/##ID##";

    BiogridClient(String resource) {
        super(resource);
    }

    @Override
    public String getAlias(Map<String, List<String>> accessions) {
        String entrezAlias = "";
        String biogridAlias = "";
        String otherAlias = "";

        for (String dbSource : accessions.keySet()) {
            if (dbSource.equalsIgnoreCase("entrez gene/locuslink") && entrezAlias.isEmpty()) {
                entrezAlias = accessions.get(dbSource).get(0);
            } else if (dbSource.equalsIgnoreCase("biogrid") && biogridAlias.isEmpty()) {
                biogridAlias = accessions.get(dbSource).get(0);
            } else if (otherAlias.isEmpty()) {
                otherAlias = accessions.get(dbSource).get(0);
            }
        }

        String rtn = otherAlias;
        if (!entrezAlias.isEmpty()) {
            rtn = entrezAlias;
        }else if(!biogridAlias.isEmpty()){
            rtn = biogridAlias;
        }

        return rtn.isEmpty() ? null : rtn.toUpperCase();
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,intact,irefindex,ddbj/embl/genbank,refseq,entrez gene/locuslink,unknown";
    }

}
