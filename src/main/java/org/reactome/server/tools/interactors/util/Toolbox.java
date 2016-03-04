package org.reactome.server.tools.interactors.util;

import org.reactome.server.tools.interactors.model.Interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

@SuppressWarnings("unused")
public class Toolbox {

    /**
     * Instead of calling the Double.valueOf(...) in a try-catch statement and many of the checks to fail due to
     * not being a number then performance of this mechanism will not be great, since you're relying upon
     * exceptions being thrown for each failure, which is a fairly expensive operation.
     * <p/>
     * An alternative approach may be to use a regular expression to check for validity of being a number:
     *
     * @return true if is Number
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Check if accession is part of an isoform in Uniprot.
     */
    public static boolean isIsoform(String acc) {
        /** This regex is based on the identifiers.org **/
        String regex = "^([A-N,R-Z][0-9][A-Z][A-Z, 0-9][A-Z, 0-9][0-9])|([O,P,Q][0-9][A-Z, 0-9][A-Z, 0-9][A-Z, 0-9][0-9])(\\-\\d+)$";
        return acc.matches(regex);
    }

    /**
     * Retrieve the database name of a given accession.
     *
     * @param acc is the Accession: Q13501 or CHEBI:16207
     * @return Uniprot or ChEBI
     */
    public static String getDatabaseName(String acc) {
        String databaseName = "UniProt";
        if (acc.toUpperCase().contains("CHEBI")) {
            databaseName = "ChEBI";
        }
        return databaseName;
    }

    /**
     * Round score having three decimal places
     * The amount of zeros represents the decimal places.
     */
    public static Double roundScore(Double score) {
        return Math.round(score * 1000d) / 1000d;
    }

    /**
     * For the same Accession retrieve the list of interactors. If the interactors are the same we will
     * remove the duplicates and keep the one of highest score.
     * <p/>
     * Requirement: Keep only the one with highest score if the interactors are the same (with different identifiers)
     * e.g CHEBI:16027 (16027) for ChEMBL.
     */
    public static List<Interaction> removeDuplicatedInteractor(List<Interaction> interactions) {
        List<Interaction> ret = new ArrayList<>(interactions.size());

        MapSet<String, Interaction> interactionMapSet = new MapSet<>();

        /** Identify potential duplicates and put in a MapSet**/
        for (Interaction interaction : interactions) {
            /** When adding in the MapSet (TreeSet impl) it already sort the interaction by score **/
            interactionMapSet.add(interaction.getInteractorB().getAcc(), interaction);
        }

        /** Interactions in the MapSet have been sorted by score as defined in the Interaction.compareTo **/
        for (String accKey : interactionMapSet.keySet()) {
            Set<Interaction> interactionSet = interactionMapSet.getElements(accKey);
            if (interactionSet.size() >= 2) { // This interaction is not unique. Let's check the score
                Interaction highScoreInteraction = null;
                for (Interaction interaction : interactionSet) {
                    highScoreInteraction = interaction;
                }
                ret.add(highScoreInteraction);
            } else {
                /** Just have only one, just add it **/
                ret.add(interactionSet.iterator().next());
            }
        }

        return ret;

    }

    public static String getAccessionURL(String acc, String resource) {
        String retURL;
        ResourceURL resourceURL = ResourceURL.getByName(resource);

        /**
         * We do not hold the Accession, then intact just assign the IntactId to the Accession.
         * If accession is an IntAct ID removes the link
         */
        if(acc.startsWith("EBI-")){
            return null;
        }

        if (isChemical(acc)) {
            retURL = resourceURL.getChemical();
        } else if(isUniprotAccession(acc)){
            retURL = InteractorConstant.DEFAULT_PROTEIN_URL;
        } else {
            retURL = resourceURL.getProtein();
        }

        if (retURL != null) {
            retURL = retURL.replace("##ID##", acc);
        }

        return retURL;
    }

    /**
     * Prepare the evidences URL based on the queried resource and the evidences list
     * Depends on the Resource, it allows querying multiple values. For certain cases
     * the resources links the evidences to another database, then it comes with the identifier,
     * which is split and then the URL is taken automatically.
     *
     * @param evidences List of interaction evidences
     * @param psicquicResource the PSICQUIC resource we are querying. STATIC resource applies straight forward.
     * @return the evidencesUrl
     */
    public static String getEvidencesURL(List<String> evidences, String psicquicResource) {
        if (evidences == null || evidences.isEmpty()) {
            return null;
        }

        // base resourceUrl - coming from PSICQUIC
        ResourceURL psicquicResourceURL = ResourceURL.getByName(psicquicResource);

        String retURL = "";

        // Check if resource has interaction URL
        if (psicquicResourceURL.hasInteractionEvidenceUrl()) {
            String term = "";
            String dbSource = psicquicResource.toUpperCase().replaceAll("-", "");

            // dbsource as the key and the evidences present in this dbsource
            MapSet<String, String> evidencesMapSet = new MapSet<>();
            for(String evidence : evidences) {
                if (evidence.contains("#")) {
                    // e.g IDBG-123123#innatedb
                    String[] eviArray = evidence.split("#");
                    evidence = eviArray[0];
                    dbSource = eviArray[1].toUpperCase();
                }
                evidencesMapSet.add(dbSource, evidence);
            }

            // get first key and value in the evidenceMap and build URL based on that.
            if(!evidencesMapSet.isEmpty()) {
                String dbSourceKey = (String)evidencesMapSet.keySet().toArray()[0];
                Set<String> evidencesSet = evidencesMapSet.getElements(dbSourceKey);

                // based on the database present in the map we get its ResourcesURL
                ResourceURL resourceURL = ResourceURL.getByName(dbSourceKey);

                // If resource is multivalued than the URL will concat the identifiers in the queryString
                if (resourceURL.isMultivalued()) {
                    String or = "";
                    for (String evidence : evidencesSet) {
                        term = term.concat(or);
                        or = "%20OR%20";
                        term = term.concat(evidence);
                    }
                } else {
                    term = evidencesSet.iterator().next();
                }

                // get the evidence url that corresponds to the dbSourceKey
                retURL = resourceURL.getInteractionEvidencesURLs().get(dbSourceKey);

                if (term == null || term.isEmpty()) {
                    retURL = null;
                } else {
                    if (retURL == null) {
                        retURL = InteractorConstant.DEFAULT_INTERACTION_URL;
                    }
                    retURL = retURL.replace("##ID##", term);
                }
            }
        }

        return retURL;
    }

    public static boolean isUniprotAccession(String accession){
        String uniprotRegex = "[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}";

        Pattern p = Pattern.compile(uniprotRegex);
        Matcher m = p.matcher(accession);

        return m.find();
    }

    /**
     * Check if the given accession is a Chemical
     * @return true if the accession represents a Chemical
     */
    public static boolean isChemical(String accession){
        return accession.startsWith("CHEBI:") || accession.startsWith("CHEMBL");
    }
}
