package org.reactome.server.tools.interactors.mapper;

/**
 * Maps an Interactor and the Interaction Id
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class InteractorMapper {

    private String acc;

    /** Interaction ID **/
    private String id;

    private Double score;

    private String alias;

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
