package org.reactome.server.interactors.psicquic.clients;

import psidev.psi.mi.tab.model.Confidence;

import java.util.List;

/**
 * Zinc resource
 */
public class ZincClient extends AbstractClient {

    ZincClient(String resource) {
        super(resource);
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,zinc,chebi,unknown";
    }

    /**
     * This resource is providing author-score as the "main" score.
     * We can only take into account author-score, miscore (provided by micluster) gives us
     * "wrong" score.
     */
    public double getScore(List<Confidence> confidenceValues) {
        String authorScore = "";
        String intactScore = "";
        String zincScore = ""; // Our suggestion to John

        for (Confidence confidence : confidenceValues) {
            String type = confidence.getType();
            if (type.equalsIgnoreCase("intact-miscore") && intactScore.isEmpty()) {
                intactScore = confidence.getValue();
            } else if (type.equalsIgnoreCase("author-score") && authorScore.isEmpty()) {
                authorScore = confidence.getValue();
            } else if (type.equalsIgnoreCase("zinc-score") && zincScore.isEmpty()) {
                zincScore = confidence.getValue();
            }
        }

        String retScore = intactScore;
        if(!zincScore.isEmpty()){
            retScore = intactScore;
        }

        return retScore.isEmpty() ? 0 : new Double(retScore);
    }
}
