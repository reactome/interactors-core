package org.reactome.server.tools.interactors.tuple.custom;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import org.apache.commons.lang.StringUtils;
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

    public TreeInteraction add(CustomInteraction customInteraction) {
        TreeInteraction interaction = new TreeInteraction();
        interaction.setScore(customInteraction.getConfidenceValue());

        /**
         * When parsing the file, SuperCSV maps the evidence into a String which is CsvInteractionEvidence.
         */
        if(StringUtils.isNotBlank(customInteraction.getCsvInteractionEvidence())) {
            interaction.addEvidence(customInteraction.getCsvInteractionEvidence());
        }

        /**
         * When we parse the PSIMITab, the cluster already maps the evidence into a list.
         */
        if(customInteraction.getEvidence() != null && customInteraction.getEvidence().size() > 0){
            interaction.setEvidence(customInteraction.getEvidence());
        }

        interaction.setIdA(customInteraction.getInteractorIdA());
        interaction.setAliasA(customInteraction.getInteractorAliasA());
        interaction.setIdB(customInteraction.getInteractorIdB());
        interaction.setAliasB(customInteraction.getInteractorAliasB());

        getOrCreateInteractions(customInteraction.getInteractorIdA()).add(interaction);

        /** Alias in an optional field **/
        if (customInteraction.getInteractorAliasA() != null) {
            getOrCreateInteractions(customInteraction.getInteractorAliasA()).add(interaction);
        }

        getOrCreateInteractions(customInteraction.getInteractorIdB()).add(interaction);

        /** Alias in an optional field **/
        if (customInteraction.getInteractorAliasB() != null) {
            getOrCreateInteractions(customInteraction.getInteractorAliasB()).add(interaction);
        }

        return interaction;
    }

    public Set<CustomInteraction> get(String identifier) {
        Set<TreeInteraction> aux = tree.getValueForExactKey(identifier);
        if (aux == null) return new HashSet<>();
        Set<CustomInteraction> rtn = new HashSet<>();
        for (TreeInteraction interaction : aux) {
            CustomInteraction i = new CustomInteraction();
            i.setInteractorIdA(interaction.getIdA());
            i.setInteractorAliasA(interaction.getAliasA());

            i.setInteractorIdB(interaction.getIdB());
            i.setInteractorAliasB(interaction.getAliasB());

            i.setConfidenceValue(interaction.getScore());
            i.setEvidence(interaction.getEvidence());

            rtn.add(i);
        }
        return rtn;
    }

    public int getInteractions() {
        Set<TreeInteraction> interactions = new HashSet<>();
        for (CharSequence charSequence : tree.getKeysStartingWith("")) {
            interactions.addAll(tree.getValueForExactKey(charSequence));
        }
        return interactions.size();
    }

    public int getInteractors() {
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
     * This method not only checks for duplicates but also updates the score
     * in order to keep the higher one and add evidences for the same interaction
     * into the same list.
     *
     * @return true if duplicate has found
     */
    public boolean checkForDuplicates(CustomInteraction interaction) {
        boolean hasDuplicate = false;

        /** Do not consider as duplicate if we just added a new evidence **/
        boolean includedEvidence = false;

        /** Check duplicates for A - A and A - B **/
        Set<TreeInteraction> ias = tree.getValueForExactKey(interaction.getInteractorIdA());
        if (ias != null) {
            String b = interaction.getInteractorIdB();
            for (TreeInteraction ia : ias) {
                String idA = ia.getIdA(), idB = ia.getIdB();
                if (b.equals(idA) || b.equals(idB)) {
                    /** Compare the score the present interaction to the new interaction **/
                    int compare = Double.compare(ia.getScore(), interaction.getConfidenceValue());
                    if (compare < 0) {
                        /** Update the score in order to keep always the **/
                        ia.setScore(interaction.getConfidenceValue());
                    }

                    /** Check evidences and keep them in a list for the same interaction pair **/
                    if (StringUtils.isNotBlank(interaction.getCsvInteractionEvidence())) {
                        if (!ia.getEvidence().contains(interaction.getCsvInteractionEvidence())) {
                            ia.addEvidence(interaction.getCsvInteractionEvidence());
                            includedEvidence = true;
                        } else {
                            hasDuplicate = true; // has same A/B/evidence
                        }
                    } else {
                        hasDuplicate = true; // has same A/B - there's no evidence
                    }

                    break;
                }
            }
        }

        /**
         * Check flipped interactions if the verification above did not find any.
         **/
        if (!hasDuplicate && !includedEvidence) {
            Set<TreeInteraction> ibs = tree.getValueForExactKey(interaction.getInteractorIdB());
            if (ibs != null) {
                String a = interaction.getInteractorIdA();
                for (TreeInteraction ib : ibs) {
                    if (a.equals(ib.getIdB()) || a.equals(ib.getIdA())) {
                        /** Compare the score the present interaction to the new interaction **/
                        int compare = Double.compare(ib.getScore(), interaction.getConfidenceValue());
                        if (compare < 0) {
                            /** Update the score in order to keep always the **/
                            ib.setScore(interaction.getConfidenceValue());
                        }

                        /** Check evidences and keep them in a list for the same interaction pair **/
                        if (StringUtils.isNotBlank(interaction.getCsvInteractionEvidence())) {
                            if (!ib.getEvidence().contains(interaction.getCsvInteractionEvidence())) {
                                ib.addEvidence(interaction.getCsvInteractionEvidence());
                            } else {
                                hasDuplicate = true; // has same A/B/evidence
                            }
                        } else {
                            hasDuplicate = true; // has same A/B - there's no evidence
                        }

                        break;
                    }
                }
            }
        }

        return hasDuplicate;
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
