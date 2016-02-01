package org.reactome.server.tools.interactors.util;

import java.text.DecimalFormat;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class Toolbox {

    /**
     * Instead of calling the Double.valueOf(...) in a try-catch statement and many of the checks to fail due to
     * not being a number then performance of this mechanism will not be great, since you're relying upon
     * exceptions being thrown for each failure, which is a fairly expensive operation.
     * <p>
     * An alternative approach may be to use a regular expression to check for validity of being a number:
     *
     * @return true if is Number
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Retrieve the identifier url of a given accession
     *
     * @param acc is the Accession: Q13501 or CHEBI:16207
     * @return the identifier url
     */
    public static String getAccessionUrl(String acc) {
        String url = InteractorConstant.INTERACTOR_BASE_URL;
        if(acc.toUpperCase().contains("CHEBI")){
            url = url.concat("chebi/").concat(acc);
        }else {
            /** Take into account the Uniprot Isoform **/
            if(isIsoform(acc)){
                url = url.concat("uniprot.isoform/").concat(acc);
            }else {
                url = url.concat("uniprot/").concat(acc);
            }
        }

        return url;
    }

    /**
     * Check if accession is part of an isoform in Uniprot.
     */
    public static boolean isIsoform(String acc){
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
        if(acc.toUpperCase().contains("CHEBI")){
            databaseName = "ChEBI";
        }
        return databaseName;
    }

    public static Double roundScore(Double score){
        DecimalFormat df = new DecimalFormat("0.###");

        return new Double(df.format(score));
    }
}
