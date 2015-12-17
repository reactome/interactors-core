package org.reactome.server.tools.interactors.model;

import java.util.ArrayList;
import java.util.List;

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
    private String acc;

    /**
     * Intact
     */
    private Long interactorResourceId;

    /**
     * Alternative ID for ID. In general they are UniprotID or RefSeq
     */
    private List<String> alternativeIds;

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
        this.acc = acc;
    }

    public Long getInteractorResourceId() {
        return interactorResourceId;
    }

    public void setInteractorResourceId(Long interactorResourceId) {
        this.interactorResourceId = interactorResourceId;
    }

   public void addAlternativeIds(String alternativeId){
       if(alternativeIds == null){
           alternativeIds = new ArrayList<String>();
       }

       alternativeIds.add(alternativeId);
   }

}
