package org.reactome.server.tools.interactors.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

@SuppressWarnings("unused")
public enum ResourceURL {

    APID(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    BIOGRID("http://www.ncbi.nlm.nih.gov/gene/##ID##", null, true, false), // psicquic does not link
    BIND(null, InteractorConstant.DEFAULT_CHEMICAL_URL, true, false), // psicquic does not link
    BINDINGDB("http://identifiers.org/bindingDB/##ID##", InteractorConstant.DEFAULT_CHEMICAL_URL, false, false),
    CHEMBL(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    //CHEMBL(InteractorConstant.DEFAULT_PROTEIN_URL, "https://www.ebi.ac.uk/chembldb/index.php/compound/inspect/##ID##", "https://www.ebi.ac.uk/chembldb/index.php/assay/inspect/##ID##"),
    DIP(InteractorConstant.DEFAULT_PROTEIN_URL, null, true, false),
    DRUGBANK(null, null, false, false),
    INNATEDB("http://innatedb.com/getGeneCard.do?id=##ID##", null, true, false),
    INNATEDBALL("http://innatedb.com/getGeneCard.do?id=##ID##", null, true, false),
    IREFINDEX(null, null, false, false),
    INTEROPORC(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    MATRIXDB(InteractorConstant.DEFAULT_PROTEIN_URL, InteractorConstant.DEFAULT_CHEMICAL_URL, false, false),
    MENTHA(InteractorConstant.DEFAULT_PROTEIN_URL, null, true, false),
    REACTOME(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    REACTOMEFIS(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    SPIKE(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    GENEMANIA(null, null, false, false), // huge, very slow
    BAR(null, null, false, false), // psicquic does not link
    EBIGOANONINTACT(InteractorConstant.DEFAULT_PROTEIN_URL, InteractorConstant.DEFAULT_CHEMICAL_URL, false, false),
    VIRHOSTNET(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    STATIC(InteractorConstant.DEFAULT_PROTEIN_URL, InteractorConstant.DEFAULT_CHEMICAL_URL, true, true);

    private String protein;
    private String chemical;
    private Map<String, String> interaction;
    private boolean multivalue;
    private boolean hasInteractionUrl;

    ResourceURL(String protein, String chemical, boolean hasInteractionUrl, boolean multivalue) {
        this.protein = protein;
        this.chemical = chemical;
        this.hasInteractionUrl = hasInteractionUrl;

        if (hasInteractionUrl) {
            this.interaction = interactionUrls();
        }

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

    public Map<String, String> getInteraction() {
        return interaction;
    }

    public void setInteraction(Map<String, String> interaction) {
        this.interaction = interaction;
    }

    public boolean isMultivalue() {
        return multivalue;
    }

    public void setMultivalue(boolean multivalue) {
        this.multivalue = multivalue;
    }

    public boolean hasInteractionUrl() {
        return hasInteractionUrl;
    }

    public void setHasInteractionUrl(boolean hasInteractionUrl) {
        this.hasInteractionUrl = hasInteractionUrl;
    }

    public static ResourceURL getByName(String name) {
        if (name == null || name.isEmpty()) return null;
        String term = name.toUpperCase().replaceAll("-", "");
        for (ResourceURL resourceURL : values()) {
            if (resourceURL.name().toUpperCase().equals(term)) return resourceURL;
        }
        return STATIC;
    }

    public Map<String, String> interactionUrls() {
        Map<String, String> urls = new HashMap<>();
        urls.put("BIOGRID", "http://thebiogrid.org/##ID##");
        urls.put("INNATEDB", "http://innatedb.com/getInteractionCard.do?idinteraction=##ID##");
        urls.put("INNATEDBALL", "http://innatedb.com/getInteractionCard.do?idinteraction=##ID##");
        urls.put("DIP", "http://dip.doe-mbi.ucla.edu/dip/DIPview.cgi?ID=##ID##");
        urls.put("BIND", "http://bond.unleashedinformatics.com/Action?identifier=bindid&idsearch=##ID##"); // bind is now redirecting to BOND (it requires access, so if someone queries BIND, we suppose the user has valid credentials
        urls.put("BINDINGDB", "http://www.bindingdb.org/bind/chemsearch/marvin/MolStructure.jsp?monomerid=##ID##");
        urls.put("INTACT", InteractorConstant.DEFAULT_INTERACTION_URL);
        urls.put("STATIC", InteractorConstant.DEFAULT_INTERACTION_URL);
        urls.put("MINT", InteractorConstant.DEFAULT_INTERACTION_URL); // mint can be linked to IntAct
        //urls.put("MINT", "http://mint.bio.uniroma2.it/mint/search/interaction.do?ac=MINT-7905142");
        urls.put("CHEMBL", "https://www.ebi.ac.uk/chembldb/index.php/assay/inspect/##ID##"); // mint can be linked to IntAct


        return urls;
    }
}
