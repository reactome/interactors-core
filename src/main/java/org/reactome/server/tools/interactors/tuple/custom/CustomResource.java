package org.reactome.server.tools.interactors.tuple.custom;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import org.reactome.server.tools.interactors.tuple.model.CustomInteraction;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class CustomResource {

    private RadixTree<Set<TreeInteraction>> tree;

    public CustomResource() {
        NodeFactory nodeFactory = new DefaultCharSequenceNodeFactory();
        this.tree = new ConcurrentRadixTree<>(nodeFactory);
    }

    public TreeInteraction add(CustomInteraction customInteraction){
        TreeInteraction interaction = new TreeInteraction();
        interaction.setScore(customInteraction.getConfidenceValue());
        //TODO: Gui... the evidence list man!
        interaction.setEvidence(null);
        interaction.setIdA(customInteraction.getInteractorIdA());
        interaction.setAliasA(customInteraction.getInteractorAliasA());
        interaction.setIdB(customInteraction.getInteractorIdB());
        interaction.setAliasB(customInteraction.getInteractorAliasB());

        getOrCreateInteractions(customInteraction.getInteractorIdA()).add(interaction);
        getOrCreateInteractions(customInteraction.getInteractorAliasA()).add(interaction);
        getOrCreateInteractions(customInteraction.getInteractorIdB()).add(interaction);
        getOrCreateInteractions(customInteraction.getInteractorAliasB()).add(interaction);

        return interaction;
    }

    public Set<CustomInteraction> get(String identifier){
        Set<TreeInteraction> aux = tree.getValueForExactKey(identifier);
        if(aux == null) return new HashSet<>();
        Set<CustomInteraction> rtn = new HashSet<>();
        for (TreeInteraction interaction : aux) {
            CustomInteraction i = new CustomInteraction();
            i.setInteractorIdA(interaction.getIdA());
            i.setAliasInteractorA(interaction.getAliasA());

            i.setInteractorIdB(interaction.getIdB());
            i.setAliasInteractorB(interaction.getAliasB());

            i.setConfidenceValue(interaction.getScore());
            //TODO: Gui... do it :)
//            i.setInteractionEvidence(interaction.getEvidence());
            rtn.add(i);
        }
        return rtn;
    }

    public int getInteractions(){
        Set<TreeInteraction> interactions = new HashSet<>();
        for (CharSequence charSequence : tree.getKeysStartingWith("")) {
            interactions.addAll(tree.getValueForExactKey(charSequence));
        }
        return interactions.size();
    }

    public int getInteractors(){
        Set<String> interactors = new HashSet<>();
        for (CharSequence charSequence : tree.getKeysStartingWith("")) {
            for (TreeInteraction interaction : tree.getValueForExactKey(charSequence)) {
                interactors.add(interaction.getIdA());
                interactors.add(interaction.getIdB());
            }
        }
        return interactors.size();
    }

    /**
     * ToDo: Needs documentation
     * @param interaction
     * @return
     */
    public boolean checkForDuplicates(CustomInteraction interaction){
        Set<TreeInteraction> ias = tree.getValueForExactKey(interaction.getInteractorIdA());
        if (ias != null) {
            String b = interaction.getInteractorIdB();
            for (TreeInteraction ia : ias) {
                String idA = ia.getIdA(), idB = ia.getIdB();
                if (b.equals(idA) || b.equals(idB)) {
                    int compare = Double.compare(ia.getScore(), interaction.getConfidenceValue());
                    if (compare < 0) ia.setScore(interaction.getConfidenceValue());
                    return true;
                }
            }
        }
        Set<TreeInteraction> ibs = tree.getValueForExactKey(interaction.getInteractorIdB());
        if (ibs != null) {
            String a = interaction.getInteractorIdA();
            for (TreeInteraction ib : ibs) {
                if (a.equals(ib.getIdB()) || a.equals(ib.getIdA()) ){
                    int compare = Double.compare(ib.getScore(), interaction.getConfidenceValue());
                    if (compare < 0) ib.setScore(interaction.getConfidenceValue());
                    return true;
                }
            }
        }
        return false;
    }

    private Set<TreeInteraction> getOrCreateInteractions(String identifier) {
        Set<TreeInteraction> interactions = tree.getValueForExactKey(identifier);
        if (interactions == null) {
            interactions = new HashSet<>();
            tree.put(identifier, interactions);
        }
        return interactions;
    }
}
