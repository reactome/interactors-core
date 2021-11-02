package org.reactome.server.interactors.psicquic.clients;

import psidev.psi.mi.tab.model.Confidence;

import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ReactomeFisClient extends AbstractClient {

    ReactomeFisClient(String resource) {
        super(resource);
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,intact,irefindex,ddbj/embl/genbank,refseq,unknown";
    }

    @Override
    public double getScore(List<Confidence> confidenceValues) {
        String nbcScore = "";
        String miscore = "";
        for (Confidence confidence : confidenceValues) {
            String type = confidence.getType();
            if (type.equalsIgnoreCase("NBC") && nbcScore.isEmpty()) {
                nbcScore = confidence.getValue();
            } else if (type.equalsIgnoreCase("miscore") && miscore.isEmpty()) {
                miscore = confidence.getValue();
            }
        }

        String retScore = miscore;
        if(!nbcScore.isEmpty()){
            retScore = nbcScore;
        }

        return retScore.isEmpty() ? 0 : parseDouble(retScore);
    }
}
