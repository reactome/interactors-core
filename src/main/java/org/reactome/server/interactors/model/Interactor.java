package org.reactome.server.interactors.model;

import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class Interactor {

    // Internal Id - autoincrement
    private Long id;

    // This is the intact id
    private String intactId;

    // Intact, UniProt, ChEBI
    private Long interactorResourceId = 0L;

    // Alternative ID for ID. In general they are UniprotID or RefSeq
    private String acc;

    // The alias is the protein name. It is the FIRST in the Aliases slot
    private String alias;

    // Taxonomy ID
    private Integer taxid;

    // Synonyms list -> remove accession if present and remove the entry that was previously assigned to alias. Unique
    private String synonyms;

    // Type, protein or gene
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        if (acc.equals("-")) {
            this.acc = this.intactId;
        } else {
            this.acc = acc.replaceAll("\"", "");
        }

    }

    public String getIntactId() {
        return intactId;
    }

    public void setIntactId(String intactId) {
        this.intactId = intactId;
    }

    public Long getInteractorResourceId() {
        return interactorResourceId;
    }

    public void setInteractorResourceId(Long interactorResourceId) {
        this.interactorResourceId = interactorResourceId;
    }

    /**
     * Get alias returns the plain alias value. We cannot parse it here,
     * for the reason this alias will be indexed.
     *
     * @return plain alias
     */
    public String getAlias() {
        if (Objects.equals(alias, acc)) return null;
        return alias == null ? null : alias.toUpperCase();
    }

    /**
     * Split alias into the species bit and get just the alias without the species.
     *
     * We agreed the requirements has different behavior in the Search (filter species) and in the JSON (no filter about species)
     *
     * @param removeOnlyHuman this flag controls either removing _HUMAN or _ANYSPECIES in the alias.
     * @return alias without species
     */
    @SuppressWarnings("unused")
    public String getAliasWithoutSpecies(boolean removeOnlyHuman) {
        String newAlias = alias;
        if (Objects.equals(alias, acc)) return null;

        if (alias == null) return null;

        if(removeOnlyHuman) {
            newAlias = alias.replace("_HUMAN", "").replace("HUMAN","").trim().replaceAll("_", " ");
        } else {
            if (alias.contains("_")) { // If alias contains 'underscore' then we split it and take only the first piece
                String[] aliasSplit = alias.split("_", 2);
                newAlias = aliasSplit[0];
            }
        }

        return newAlias;
    }

    public void setAlias(String alias) {
        this.alias = null;
        if(StringUtils.isNotEmpty(alias)){
            //if (alias != null) {
            this.alias = alias.toUpperCase().replaceAll("\"", "");
        }
    }

    @Override
    public String toString() {
        return "Interactor{" +
                "id=" + id +
                ", intactId='" + intactId + '\'' +
                ", interactorResourceId=" + interactorResourceId +
                ", acc='" + acc + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interactor that = (Interactor) o;

        return !(acc != null ? !acc.equals(that.acc) : that.acc != null);

    }

    @Override
    public int hashCode() {
        return acc != null ? acc.hashCode() : 0;
    }

    public Integer getTaxid() {
        return taxid;
    }

    public void setTaxid(Integer taxid) {
        this.taxid = taxid;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        if (synonyms != null) {
            this.synonyms = synonyms.replaceAll("\"", "");
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
