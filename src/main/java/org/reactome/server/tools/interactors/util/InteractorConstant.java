package org.reactome.server.tools.interactors.util;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorConstant {
    public static final String STATIC = "static";

    public static final String PSICQUIC = "psicquic";

    public static final Double MINIMUM_VALID_SCORE = 0.45;

    public static final String INTERACTOR_BASE_URL = "http://identifiers.org/";

    public static final String DEFAULT_PROTEIN_URL = "http://www.uniprot.org/uniprot/##ID##";
    public static final String DEFAULT_CHEMICAL_URL = "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=##ID##";
    public static final String DEFAULT_INTERACTION_URL = "http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=##ID##";
}
