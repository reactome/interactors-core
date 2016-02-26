package org.reactome.server.tools.interactors.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

@SuppressWarnings("unused")
public enum ResourceURL {

    APID(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    BIOGRID(InteractorConstant.BIOGRID_MOLECULE_URL, null, true, false), // psicquic does not link
    BIND(null, InteractorConstant.DEFAULT_CHEMICAL_URL, true, false), // psicquic does not link
    BINDINGDB(InteractorConstant.BINDINGDB_MOLECULE_URL, InteractorConstant.DEFAULT_CHEMICAL_URL, false, false),
    CHEMBL(InteractorConstant.DEFAULT_PROTEIN_URL, null, false, false),
    //CHEMBL(InteractorConstant.DEFAULT_PROTEIN_URL, InteractorConstant.CHEMBL_MOLECULE_URL, true, false), // this is deactivated for a while.
    DIP(InteractorConstant.DEFAULT_PROTEIN_URL, null, true, false),
    DRUGBANK(null, null, false, false),
    INNATEDB(InteractorConstant.INNATEDB_MOLECULE_URL, null, true, false),
    INNATEDBALL(InteractorConstant.INNATEDB_MOLECULE_URL, null, true, false),
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
    private Map<String, String> interactionEvidencesURLs;
    private boolean multivalued;
    private boolean hasInteractionEvidenceUrl;

    ResourceURL(String protein, String chemical, boolean hasInteractionEvidenceUrl, boolean multivalued) {
        this.protein = protein;
        this.chemical = chemical;
        this.hasInteractionEvidenceUrl = hasInteractionEvidenceUrl;

        if (hasInteractionEvidenceUrl) {
            this.interactionEvidencesURLs = interactionEvidencesURLs();
        }

        this.multivalued = multivalued;
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

    public Map<String, String> getInteractionEvidencesURLs() {
        return interactionEvidencesURLs;
    }

    public void setInteractionEvidencesURLs(Map<String, String> interactionEvidencesURLs) {
        this.interactionEvidencesURLs = interactionEvidencesURLs;
    }

    public boolean isMultivalued() {
        return multivalued;
    }

    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    }

    public boolean hasInteractionEvidenceUrl() {
        return hasInteractionEvidenceUrl;
    }

    public void setHasInteractionEvidenceUrl(boolean hasInteractionEvidenceUrl) {
        this.hasInteractionEvidenceUrl = hasInteractionEvidenceUrl;
    }

    public static ResourceURL getByName(String name) {
        if (name == null || name.isEmpty()) return null;
        String term = name.toUpperCase().replaceAll("-", "");
        for (ResourceURL resourceURL : values()) {
            if (resourceURL.name().toUpperCase().equals(term)) return resourceURL;
        }
        return STATIC;
    }

    /**
     * Mapping evidences URLs
     * For certain resources we have different databases to build the evidence URL
     */
    public Map<String, String> interactionEvidencesURLs() {
        Map<String, String> urls = new HashMap<>();
        urls.put("BIOGRID", InteractorConstant.BIOGRID_EVIDENCE_URL);
        urls.put("INNATEDB", InteractorConstant.INNATEDB_EVIDENCE_URL);
        urls.put("INNATEDBALL", InteractorConstant.INNATEDBALL_EVIDENCE_URL);
        urls.put("DIP", InteractorConstant.DIP_EVIDENCE_URL);
        urls.put("BIND", InteractorConstant.BIND_EVIDENCE_URL); // bind is now redirecting to BOND (it requires access, so if someone queries BIND, we suppose the user has valid credentials
        urls.put("BINDINGDB", InteractorConstant.BINDINGDB_EVIDENCE_URL);
        urls.put("INTACT", InteractorConstant.DEFAULT_INTERACTION_URL);
        urls.put("STATIC", InteractorConstant.DEFAULT_INTERACTION_URL);
        urls.put("MINT", InteractorConstant.DEFAULT_INTERACTION_URL); // mint can be linked to IntAct
        //urls.put("MINT", "http://mint.bio.uniroma2.it/mint/search/interaction.do?ac=MINT-7905142");
        urls.put("CHEMBL", InteractorConstant.CHEMBL_EVIDENCE_URL);

        return urls;
    }
}
