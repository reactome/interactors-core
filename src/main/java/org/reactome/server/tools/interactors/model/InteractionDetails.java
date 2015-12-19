package org.reactome.server.tools.interactors.model;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionDetails {

    /**
     * Internal autoincrement id
     */
    private Long id;

    /**
     * Intenal bi-directional identifier
     */
    private Long interactionId;

    /**
     * Interaction AC: EBI-XXXXX
     */
    private String interactionAc;

    public InteractionDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(Long interactionId) {
        this.interactionId = interactionId;
    }

    public String getInteractionAc() {
        return interactionAc;
    }

    public void setInteractionAc(String interactionAc) {
        this.interactionAc = interactionAc;
    }

    @Override
    public String toString() {
        return "InteractionDetails{" +
                "id=" + id +
                ", interactionId=" + interactionId +
                ", interactionAc='" + interactionAc + '\'' +
                '}';
    }
}
