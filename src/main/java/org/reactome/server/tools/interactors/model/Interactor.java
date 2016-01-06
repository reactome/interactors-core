package org.reactome.server.tools.interactors.model;

import java.sql.Timestamp;

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
    private Long interactorResourceId = new Long(0);

    /**
     * Timestamp
     */
    private Timestamp createDate;

    /**
     * Alternative ID for ID. In general they are UniprotID or RefSeq
     */
    private String acc;

    /**
     * The alias is basically the protein name. A text representation for the protein.
     * This is going to be in the synonym list.
     */
    private String alias;

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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
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
                ", createDate=" + createDate +
                ", acc='" + acc + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
