package org.reactome.server.tools.interactors.tuple.custom;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class TreeInteraction {
    private double score;
    private List<String> evidence;

    private String idA;
    private String aliasA;
    private String idB;
    private String aliasB;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public String getIdA() {
        return idA;
    }

    public void setIdA(String idA) {
        this.idA = idA;
    }

    public String getAliasA() {
        return aliasA;
    }

    public void setAliasA(String aliasA) {
        this.aliasA = aliasA;
    }

    public String getIdB() {
        return idB;
    }

    public void setIdB(String idB) {
        this.idB = idB;
    }

    public String getAliasB() {
        return aliasB;
    }

    public void setAliasB(String aliasB) {
        this.aliasB = aliasB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeInteraction that = (TreeInteraction) o;

        // Custom equality check here.
        boolean equals = this.idA.equals(that.idA) && this.idB.equals(that.idB);

        if (!equals) {
            equals = this.idA.equals(that.idB) && this.idB.equals(that.idA);
        }

        return equals;

    }

    @Override
    public int hashCode() {
        try {
            return 31 * idA.hashCode() * idB.hashCode();
        } catch (NullPointerException e) {
            // When using kryo, the hashCode is called before the attributes
            // are populated. Calling the super solves the problem.
            return super.hashCode();
        }
    }
}
