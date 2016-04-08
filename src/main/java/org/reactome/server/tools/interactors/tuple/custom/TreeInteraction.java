package org.reactome.server.tools.interactors.tuple.custom;

import com.googlecode.concurrenttrees.radix.node.Node;

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

    private Node nodeIdA;
    private Node nodeAliasA;

    private Node nodeIdB;
    private Node nodeAliasB;

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
        return nodeIdA == null ? idA : nodeIdA.getIncomingEdge().toString();
    }

    public void setIdA(Node nodeIdA) {
        this.nodeIdA = nodeIdA;
        this.idA = null;
    }

    public void setIdA(String idA) {
        this.idA = idA;
    }

    public String getAliasA() {
        return nodeAliasA == null ? aliasA : nodeAliasA.getIncomingEdge().toString();
    }

    public void setAliasA(Node nodeAliasA) {
        this.nodeAliasA = nodeAliasA;
        this.aliasA = null;
    }

    public void setAliasA(String aliasA) {
        this.aliasA = aliasA;
    }

    public String getIdB() {
        return nodeIdB == null ? idB : nodeIdB.getIncomingEdge().toString();
    }

    public void setIdB(Node nodeIdB) {
        this.nodeIdB = nodeIdB;
        this.idB = null;
    }

    public void setIdB(String idB) {
        this.idB = idB;
    }

    public String getAliasB() {
        return nodeAliasB == null ? aliasB : nodeAliasB.getIncomingEdge().toString();
    }

    public void setAliasB(Node nodeAliasB) {
        this.nodeAliasB = nodeAliasB;
        this.aliasB = null;
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
        boolean equals = this.getIdA().equals(that.getIdA()) && this.getIdB().equals(that.getIdB());

        if (!equals) {
            equals = this.getIdA().equals(that.getIdB()) && this.getIdB().equals(that.getIdA());
        }

        return equals;

    }

    @Override
    public int hashCode() {
        try {
            return 31 * getIdA().hashCode() * getIdB().hashCode();
        } catch (NullPointerException e) {
            // When using kryo, the hashCode is called before
            // attributes are populated. Calling the super
            // solves the problem
            return super.hashCode();
        }
    }
}
