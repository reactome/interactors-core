package org.reactome.server.tools.interactors.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class Interaction {

    /**
     * Internal autoincrement identifier
     */
    private Long id;

    private Interactor interactorA;
    private Interactor interactorB;

    private double authorScore;
    private double intactScore;

    private Long interactionResourceId;

    private List<InteractionDetails> interactionDetailsList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<InteractionDetails> getInteractionDetailsList() {
        return interactionDetailsList;
    }

    public void setInteractionDetailsList(List<InteractionDetails> interactionDetailsList) {
        this.interactionDetailsList = interactionDetailsList;
    }

    public void addInteractionDetails(InteractionDetails interactionDetails){
        if(interactionDetailsList == null){
            interactionDetailsList = new ArrayList<>();
        }
        interactionDetailsList.add(interactionDetails);
    }

    public double getAuthorScore() {
        return authorScore;
    }

    public void setAuthorScore(double authorScore) {
        this.authorScore = authorScore;
    }

    public double getIntactScore() {
        return intactScore;
    }

    public void setIntactScore(double intactScore) {
        this.intactScore = intactScore;
    }

    public Long getInteractionResourceId() {
        return interactionResourceId;
    }

    public void setInteractionResourceId(Long interactionResourceId) {
        this.interactionResourceId = interactionResourceId;
    }

    @Override
    public String toString() {
        return "Interaction{" +
                "id=" + id +
                ", interactorA=" + interactorA +
                ", interactorB=" + interactorB +
                ", interactionDetailsList=" + interactionDetailsList +
                '}';
    }
}
