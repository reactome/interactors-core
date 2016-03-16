package org.reactome.server.tools.interactors.tuple.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public enum ColumnDefinition {

    ID_A("interactorIdA", true),
    ID_B("interactorIdB", true),
    ALIAS_A("aliasInteractorA", false),
    ALIAS_B("aliasInteractorB", false),
    TAX_ID_A("taxonomyIdInteractorA", false),
    TAX_ID_B("taxonomyIdInteractorB", false),
    EVIDENCE("interactionIdentifier", false),
    SCORE("confidenceValue", false);

    /**
     * It must have the same name as it has in the CustomInteraction
     **/
    final public String attribute;

    /**
     * Assign true to make an attribute as mandatory
     **/
    final public boolean mandatory;

    ColumnDefinition(String attribute, boolean mandatory) {
        this.attribute = attribute;
        this.mandatory = mandatory;
    }

    /**
     * Retrieve mandatory columns
     **/
    public static List<ColumnDefinition> getMandatoryColumns() {
        List<ColumnDefinition> rtnMandatories = new ArrayList<>();
        ColumnDefinition[] all = values();
        for (ColumnDefinition columnDefinition : all) {
            if (columnDefinition.mandatory) {
                rtnMandatories.add(columnDefinition);
            }
        }
        return rtnMandatories;
    }
}

