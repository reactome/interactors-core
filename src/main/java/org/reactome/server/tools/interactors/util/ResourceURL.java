package org.reactome.server.tools.interactors.util;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

@SuppressWarnings("unused")
public enum ResourceURL {

    APID(InteractorConstant.DEFAULT_PROTEIN_URL, null, null),
    BIOGRID("http://identifiers.org/biogrid/##ID##", null, null),
    BIND("http://identifiers.org/bind/##ID##", null, null),
    BINDINGDB("http://identifiers.org/bindingDB/##ID##", null, null),
    CHEMBL(null, null, null),
    //CHEMBL(InteractorConstant.DEFAULT_PROTEIN_URL, "https://www.ebi.ac.uk/chembldb/index.php/compound/inspect/##ID##", "https://www.ebi.ac.uk/chembldb/index.php/assay/inspect/##ID##"),
    DIP("http://identifiers.org/dip/##ID##", null, null),
    DRUGBANK(null, null, null),
    INNATEDB(null, null, null),
    INNATEDBALL(null, null, null),
    IREFINDEX(null, null, null),
    INTEROPORC(null, null, null),
    MATRIXDB(null, null, null),
    MENTHA(null, null, null), // tricky
    REACTOME(null, null, null),
    REACTOMEFIS(null, null, null),
    GENEMANIA(null, null, null),// tricky
    BAR(null, null, null),
    EBIGOANONINTACT(null, null, null),
    VIRHOSTNET(null, null, null),
    DEFAULT(InteractorConstant.DEFAULT_PROTEIN_URL, InteractorConstant.DEFAULT_CHEMICAL_URL, InteractorConstant.DEFAULT_INTERACTION_URL);

    private String protein;
    private String chemical;
    private String interaction;

    ResourceURL(String protein, String chemical, String interaction) {
        this.protein = protein;
        this.chemical = chemical;
        this.interaction = interaction;
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

    public static ResourceURL getByName(String name) {
        if (name == null || name.isEmpty()) return null;
        String term = name.toUpperCase().replaceAll("-", "");
        for (ResourceURL resourceURL : values()) {
            if (resourceURL.name().toUpperCase().equals(term)) return resourceURL;
        }
        return DEFAULT;
    }
}
