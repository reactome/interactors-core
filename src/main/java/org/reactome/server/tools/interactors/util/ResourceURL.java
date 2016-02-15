package org.reactome.server.tools.interactors.util;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

@SuppressWarnings("unused")
public enum ResourceURL {

    DIP("http://identifiers/dip/##ID##", null, null),
    BIOGRID("http://identifiers/biogrid/##ID##", null, null),
    BIND("http://identifiers/bind/##ID##", null, null);

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

    public static ResourceURL getByName(String name){
        if (name == null || name.isEmpty()) return null;
        String term = name.toUpperCase();
        for (ResourceURL resourceURL : values()) {
            if(resourceURL.name().toUpperCase().equals(term)) return resourceURL;
        }
        return null;
    }
}
