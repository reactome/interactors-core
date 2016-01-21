package org.reactome.server.tools.interactors.model;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class Interactor {

    /**
     * Internal Id - autoincrement
     */
    private Long id;

    /**
     * This is the intact id
     */
    private String intactId;

    /**
     * Intact
     */
    private Long interactorResourceId = 0L;

    /**
     * Alternative ID for ID. In general they are UniprotID or RefSeq
     */
    private String acc;

    /**
     * The alias is basically the protein name. A text representation for the protein.
     * This is going to be in the synonym list.
     */
    private String alias;

    /**
     * Taxonomy ID
     */
    private Integer taxid;

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
        if(acc.equals("-")){
            this.acc = this.intactId;
        }else {
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias.replaceAll("\"", "");
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
}
