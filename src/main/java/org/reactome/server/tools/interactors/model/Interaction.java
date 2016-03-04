package org.reactome.server.tools.interactors.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class Interaction implements Comparable<Interaction> {

    /**
     * Internal autoincrement identifier
     */
    private Long id;

    private Interactor interactorA;
    private Interactor interactorB;

    private double authorScore;
    private Double intactScore;

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

    public Double getIntactScore() {
        return intactScore;
    }

    public void setIntactScore(Double intactScore) {
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

    @Override
    public int compareTo(Interaction o) {
        return this.intactScore.compareTo(o.intactScore);
    }
}
