package org.reactome.server.tools.interactors.psicquic.clients;

import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ChemblClient extends AbstractClient {

    private final int CHEMICAL_ALIAS_SIZE_THRESHOLD = 15;

    ChemblClient(String resource) {
        super(resource);
    }

    @Override
    public String getAlias(Map<String, List<String>> accessions) {
        String psimiAlias = "";
        String uniprotAlias = "";

        for (String dbSource : accessions.keySet()) {
            if (dbSource.equalsIgnoreCase("psi-mi") && psimiAlias.isEmpty()) {
                psimiAlias = accessions.get(dbSource).get(0);
            } else if (dbSource.equalsIgnoreCase("uniprotkb") && uniprotAlias.isEmpty()) {
                uniprotAlias = accessions.get(dbSource).get(0);
            } else if (dbSource.equalsIgnoreCase("unknown")) {
                //In CHEMBL there are many columns named unknown
                //It looks like the content of the first one has several sections
                //separated by "|" (pipe) and in the very first one they include
                //the chemical name (and this is what we use as ALIAS for this case
                //gsviteri: This separated by pipe format is applied for the raw data. As we are
                //querying the clustered results, the data is a map. No split needed.

                for (String alias : accessions.get(dbSource)) {
                    if(alias.toLowerCase().startsWith("inchi=") || alias.toLowerCase().startsWith("chembl") || alias.length() >= CHEMICAL_ALIAS_SIZE_THRESHOLD) {
                        // Skip if alias starts with InChI=, chembl or has more characters than CHEMICAL_ALIAS_SIZE_THRESHOLD
                        continue;
                    }

                    return alias;
                }
            }
        }

        //This is only reached in case there is no "unknown" key in the response
        String rtn = psimiAlias;
        if (!uniprotAlias.isEmpty()) {
            rtn = uniprotAlias;
        }

        return rtn.isEmpty() ? null : rtn.toUpperCase();
    }
}
