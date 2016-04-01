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

    public void addInteractionDetails(InteractionDetails interactionDetails) {
        if (interactionDetailsList == null) {
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
    public int compareTo(Interaction that) {
        // Taking into account that the score can be null in the custom interactions.
        if (this.intactScore == null) {
            if (that.intactScore == null) {
                return 0; //equal
            } else {
                return -1; // null is before other strings
            }
        } else {// this.member != null
            if (that.intactScore == null) {
                return 1;  // all other strings are after null
            } else {
                return this.intactScore.compareTo(that.intactScore);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interaction that = (Interaction) o;

        if (interactorA != null ? !interactorA.equals(that.interactorA) : that.interactorA != null) return false;
        return !(interactorB != null ? !interactorB.equals(that.interactorB) : that.interactorB != null);

    }

    @Override
    public int hashCode() {
        int result = interactorA != null ? interactorA.hashCode() : 0;
        result = 31 * result + (interactorB != null ? interactorB.hashCode() : 0);
        return result;
    }
}
