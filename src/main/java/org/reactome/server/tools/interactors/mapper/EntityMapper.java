package org.reactome.server.tools.interactors.mapper;

import java.util.List;

/**
 * Maps an Entity which is the accessionA and its interactions list.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class EntityMapper {

    private String acc;
    private Integer count;
    private List<InteractorMapper> interactors;

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<InteractorMapper> getInteractors() {
        return interactors;
    }

    public void setInteractors(List<InteractorMapper> interactors) {
        this.interactors = interactors;
    }
}
