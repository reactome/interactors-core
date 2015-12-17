package org.reactome.server.tools.interactors.model;

import java.util.UUID;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class Interaction {
    private Long id;
    private String interactionId;
    private Interactor interactorA;
    private Interactor interactorB;

    private double authorScore;
    private double intactScore;

    private Long interactionResourceId;

    public Interaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }

    public Interactor getInteractorA() {
        return interactorA;
    }

    public void setInteractorA(Interactor interactorA) {
        this.interactorA = interactorA;
    }

    public Interactor getInteractorB() {
        return interactorB;
    }

    public void setInteractorB(Interactor interactorB) {
        this.interactorB = interactorB;
    }

    public double getAuthorScore() {
        return authorScore;
    }

    public void setAuthorScore(double authorScore) {
        this.authorScore = authorScore;
    }

    public Long getInteractionResourceId() {
        return interactionResourceId;
    }

    public void setInteractionResourceId(Long interactionResourceId) {
        this.interactionResourceId = interactionResourceId;
    }

    public double getIntactScore() {
        return intactScore;
    }

    public void setIntactScore(double intactScore) {
        this.intactScore = intactScore;
    }
}
