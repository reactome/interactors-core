package org.reactome.server.tools.interactors.mapper;

import java.util.List;
import java.util.Map;

/**
 * Maps an Interaction in the JSON output
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class InteractionMapper {

    private String resource;

    private String interactorUrl;

    private String interactionUrl;

    private List<EntityMapper> entities;

    //private Map<String, SynonymMapper> synonym;


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getInteractorUrl() {
        return interactorUrl;
    }

    public void setInteractorUrl(String interactorUrl) {
        this.interactorUrl = interactorUrl;
    }

    public String getInteractionUrl() {
        return interactionUrl;
    }

    public void setInteractionUrl(String interactionUrl) {
        this.interactionUrl = interactionUrl;
    }

    public List<EntityMapper> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityMapper> entities) {
        this.entities = entities;
    }

//    public Map<String, SynonymMapper> getSynonym() {
//        return synonym;
//    }
//
//    public void setSynonym(Map<String, SynonymMapper> synonym) {
//        this.synonym = synonym;
//    }

}