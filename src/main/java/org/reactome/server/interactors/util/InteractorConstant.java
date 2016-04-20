package org.reactome.server.interactors.util;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorConstant {
    public static final String STATIC = "static";

    public static final String PSICQUIC = "psicquic";

    public static final Double MINIMUM_VALID_SCORE = 0.45;

    public static final Double TUPLE_DEFAULT_SCORE = 1.0;
    public static final String TUPLE_PREFIX = "PSI-";
    public static final String GENERIC_CLIENT_FACTORY = "GENERIC";

    //public static final String INTERACTOR_BASE_URL = "http://identifiers.org/";

    public static final String DEFAULT_PROTEIN_URL = "http://www.uniprot.org/uniprot/##ID##";
    public static final String DEFAULT_CHEMICAL_URL = "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=##ID##";
    public static final String DEFAULT_INTERACTION_URL = "http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=##ID##";

    /** Evidences URL **/
    public static final String BIOGRID_EVIDENCE_URL = "http://thebiogrid.org/##ID##";
    public static final String INNATEDB_EVIDENCE_URL = "http://innatedb.com/getInteractionCard.do?idinteraction=##ID##";
    public static final String INNATEDBALL_EVIDENCE_URL = "http://innatedb.com/getInteractionCard.do?idinteraction=##ID##";
    public static final String DIP_EVIDENCE_URL = "http://dip.doe-mbi.ucla.edu/dip/DIPview.cgi?ID=##ID##";
    public static final String BIND_EVIDENCE_URL = "http://bond.unleashedinformatics.com/Action?identifier=bindid&idsearch=##ID##";
    public static final String BINDINGDB_EVIDENCE_URL = "http://www.bindingdb.org/bind/chemsearch/marvin/MolStructure.jsp?monomerid=##ID##";
    public static final String CHEMBL_EVIDENCE_URL = "https://www.ebi.ac.uk/chembldb/index.php/assay/inspect/##ID##";

    /** Molecules URL **/
    public static final String INNATEDB_MOLECULE_URL = "http://innatedb.com/getGeneCard.do?id=##ID##";
    public static final String BIOGRID_MOLECULE_URL = "http://www.ncbi.nlm.nih.gov/gene/##ID##";
    public static final String BINDINGDB_MOLECULE_URL = "http://www.bindingdb.org/bind/chemsearch/marvin/MolStructure.jsp?monomerid=##ID##";
    public static final String CHEMBL_MOLECULE_URL = "https://www.ebi.ac.uk/chembldb/index.php/compound/inspect/##ID##";

}
