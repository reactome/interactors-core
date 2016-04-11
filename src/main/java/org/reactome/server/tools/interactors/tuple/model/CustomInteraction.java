package org.reactome.server.tools.interactors.tuple.model;

import java.util.LinkedList;
import java.util.List;

/**
 * After parsing the custom file submitted by the user, the
 * CustomInteraction class is the common format that we use
 * to store and later on retrieve and query on it.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class CustomInteraction {

    /**
     * Rename attributes, getter and setter here may have consequences in the parser
     */
    private String interactorIdA;
    private String interactorIdB;
    private String interactorAliasA;
    private String interactorAliasB;
    private Double confidenceValue;

    /**
     * Interaction Evidence received from custom tuple file. Will be added in the evidences later on
     */
    private String csvInteractionEvidence;

    /**
     * List of all evidence for the same interaction
     * note: this is not mapped by SuperCSV.
     */
    private List<String> evidence;

    public String getInteractorIdA() {
        return interactorIdA;
    }

    public void setInteractorIdA(String interactorIdA) {
        this.interactorIdA = interactorIdA;
    }

    public String getInteractorIdB() {
        return interactorIdB;
    }

    public void setInteractorIdB(String interactorIdB) {
        this.interactorIdB = interactorIdB;
    }

    public String getInteractorAliasA() {
        return interactorAliasA;
    }

    public void setInteractorAliasA(String interactorAliasA) {
        this.interactorAliasA = interactorAliasA;
    }

    public String getInteractorAliasB() {
        return interactorAliasB;
    }

    public void setInteractorAliasB(String interactorAliasB) {
        this.interactorAliasB = interactorAliasB;
    }

    public Double getConfidenceValue() {
        return confidenceValue;
    }

    public void setConfidenceValue(Double confidenceValue) {
        this.confidenceValue = confidenceValue;
    }

    /** SuperCSV library maps attribute using String **/
    public void setConfidenceValue(String confidenceValue) {
        setConfidenceValue(Double.valueOf(confidenceValue));
    }

    /**
     * Single evidence sent by user in the tuple file.
     * To get access to evidence please refer to getEvidence()
     */
    public String getCsvInteractionEvidence() {
        return csvInteractionEvidence;
    }

    public void setCsvInteractionEvidence(String csvInteractionEvidence) {
        this.csvInteractionEvidence = csvInteractionEvidence;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public void addEvidence(String evidence) {
        if (this.evidence == null) {
            this.evidence = new LinkedList<>();
        }

        this.evidence.add(evidence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomInteraction that = (CustomInteraction) o;

        // Custom equality check here.
        boolean equals = this.interactorIdA.equals(that.interactorIdA) && this.interactorIdB.equals(that.interactorIdB);

        if (!equals) {
            equals =  this.interactorIdA.equals(that.interactorIdB) && this.interactorIdB.equals(that.interactorIdA);
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return interactorIdA.hashCode() * interactorIdB.hashCode();
    }

    @Override
    public String toString() {
        return "CustomInteraction{" +
                "interactorIdA='" + interactorIdA + '\'' +
                ", interactorIdB='" + interactorIdB + '\'' +
                ", interactorAliasA='" + interactorAliasA + '\'' +
                ", interactorAliasB='" + interactorAliasB + '\'' +
                ", evidence='" + evidence + '\'' +
                ", confidenceValue='" + confidenceValue + '\'' +
                '}';
    }
}
