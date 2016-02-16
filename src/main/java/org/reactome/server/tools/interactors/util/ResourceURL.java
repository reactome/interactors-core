package org.reactome.server.tools.interactors.util;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

@SuppressWarnings("unused")
public enum ResourceURL {

    APID(InteractorConstant.DEFAULT_PROTEIN_URL, null, null),
    BIOGRID("http://identifiers.org/biogrid/##ID##", null, null),
    BIND("http://identifiers.org/bind/##ID##", null, null),
    BINDINGDB("http://identifiers.org/bindingDB/##ID##", InteractorConstant.DEFAULT_CHEMICAL_URL, null),
    CHEMBL(InteractorConstant.DEFAULT_PROTEIN_URL, null, null),
    //CHEMBL(InteractorConstant.DEFAULT_PROTEIN_URL, "https://www.ebi.ac.uk/chembldb/index.php/compound/inspect/##ID##", "https://www.ebi.ac.uk/chembldb/index.php/assay/inspect/##ID##"),
    DIP(InteractorConstant.DEFAULT_PROTEIN_URL, null, "http://identifiers.org/dip/##ID##"),
    DRUGBANK(null, null, null),
    INNATEDB(null, null, null),
    INNATEDBALL(null, null, null),
    IREFINDEX(null, null, null),
    INTEROPORC(null, null, null),
    MATRIXDB(null, null, null),
    MENTHA(InteractorConstant.DEFAULT_PROTEIN_URL, null, "http://identifiers.org/##RESOURCE##/##ID##"), // tricky
    REACTOME(InteractorConstant.DEFAULT_PROTEIN_URL, null, null),
    REACTOMEFIS(InteractorConstant.DEFAULT_PROTEIN_URL, null, null),
    GENEMANIA(null, null, null),// tricky
    BAR(null, null, null), // psicquic does not link here
    EBIGOANONINTACT(InteractorConstant.DEFAULT_PROTEIN_URL, InteractorConstant.DEFAULT_CHEMICAL_URL, null),
    VIRHOSTNET(null, null, null),
    DEFAULT(InteractorConstant.DEFAULT_PROTEIN_URL, InteractorConstant.DEFAULT_CHEMICAL_URL, InteractorConstant.DEFAULT_INTERACTION_URL, true);

    private String protein;
    private String chemical;
    private String interaction;
    private boolean multivalue;

    ResourceURL(String protein, String chemical, String interaction) {
        this.protein = protein;
        this.chemical = chemical;
        this.interaction = interaction;
        this.multivalue = false;
    }

    ResourceURL(String protein, String chemical, String interaction, boolean multivalue) {
        this.protein = protein;
        this.chemical = chemical;
        this.interaction = interaction;
        this.multivalue = multivalue;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getChemical() {
        return chemical;
    }

    public void setChemical(String chemical) {
        this.chemical = chemical;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

    public boolean isMultivalue() {
        return multivalue;
    }

    public void setMultivalue(boolean multivalue) {
        this.multivalue = multivalue;
    }

    public static ResourceURL getByName(String name) {
        if (name == null || name.isEmpty()) return null;
        String term = name.toUpperCase().replaceAll("-", "");
        for (ResourceURL resourceURL : values()) {
            if (resourceURL.name().toUpperCase().equals(term)) return resourceURL;
        }
        return DEFAULT;
    }
}
