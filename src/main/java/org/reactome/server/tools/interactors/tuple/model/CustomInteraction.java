package org.reactome.server.tools.interactors.tuple.model;

/**
 *
 *
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class CustomInteraction {

    public enum CustomInteractionColumn {
        ID_INTERACTOR_A,  // add COLUMN DEFINITION NAMES which might be mandatory in the file...
        ID_INTERACTOR_B,
        ALTERNATIVE_INTERACTOR_A,
        ALTERNATIVE_INTERACTOR_B,
        ALIAS_INTERACTOR_A,
        ALIAS_INTERACTOR_B,
        INTERACTION_DETECTION_METHOD,
        PUBLICATION_1ST_AUTHOR,
        PUBLICATION_IDENTIFIER,
        TAXID_INTERACTOR_A,
        TAXID_INTERACTOR_B,
        INTERACTION_TYPE,
        SOURCE_DATABASE,
        INTERACTION_IDENTIFIER,
        CONFIDENCE_VALUE;

//        final String columnName;
//        CustomInteractionIndex(String columnName){
//            this.columnName = columnName;
//        }

    }

    /**
     * Rename attributes, getter and setter here may have consequences in the parser
     */
    private String interactorIdA;
    private String interactorIdB;
    private String alternativeInteractorA;
    private String alternativeInteractorB;
    private String aliasInteractorA;
    private String aliasInteractorB;
    private String interactionDetectionMethod;
    private String publication1stAuthor;
    private String publicationIdentifier;
    private String taxonomyIdInteractorA;
    private String taxonomyIdInteractorB;
    private String interactionType;
    private String sourceDatabase;
    private String interactionIdentifier;
    private String confidenceValue;

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

    public String getAlternativeInteractorA() {
        return alternativeInteractorA;
    }

    public void setAlternativeInteractorA(String alternativeInteractorA) {
        this.alternativeInteractorA = alternativeInteractorA;
    }

    public String getAlternativeInteractorB() {
        return alternativeInteractorB;
    }

    public void setAlternativeInteractorB(String alternativeInteractorB) {
        this.alternativeInteractorB = alternativeInteractorB;
    }

    public String getAliasInteractorA() {
        return aliasInteractorA;
    }

    public void setAliasInteractorA(String aliasInteractorA) {
        this.aliasInteractorA = aliasInteractorA;
    }

    public String getAliasInteractorB() {
        return aliasInteractorB;
    }

    public void setAliasInteractorB(String aliasInteractorB) {
        this.aliasInteractorB = aliasInteractorB;
    }

    public String getInteractionDetectionMethod() {
        return interactionDetectionMethod;
    }

    public void setInteractionDetectionMethod(String interactionDetectionMethod) {
        this.interactionDetectionMethod = interactionDetectionMethod;
    }

    public String getPublication1stAuthor() {
        return publication1stAuthor;
    }

    public void setPublication1stAuthor(String publication1stAuthor) {
        this.publication1stAuthor = publication1stAuthor;
    }

    public String getPublicationIdentifier() {
        return publicationIdentifier;
    }

    public void setPublicationIdentifier(String publicationIdentifier) {
        this.publicationIdentifier = publicationIdentifier;
    }

    public String getTaxonomyIdInteractorA() {
        return taxonomyIdInteractorA;
    }

    public void setTaxonomyIdInteractorA(String taxonomyIdInteractorA) {
        this.taxonomyIdInteractorA = taxonomyIdInteractorA;
    }

    public String getTaxonomyIdInteractorB() {
        return taxonomyIdInteractorB;
    }

    public void setTaxonomyIdInteractorB(String taxonomyIdInteractorB) {
        this.taxonomyIdInteractorB = taxonomyIdInteractorB;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public String getSourceDatabase() {
        return sourceDatabase;
    }

    public void setSourceDatabase(String sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
    }

    public String getInteractionIdentifier() {
        return interactionIdentifier;
    }

    public void setInteractionIdentifier(String interactionIdentifier) {
        this.interactionIdentifier = interactionIdentifier;
    }

    public String getConfidenceValue() {
        return confidenceValue;
    }

    public void setConfidenceValue(String confidenceValue) {
        this.confidenceValue = confidenceValue;
    }

    public void flip(String a, String b){
        interactorIdA = b;
        interactorIdB = a;
    }

    public void flipBack(String a, String b){
        interactorIdA = a;
        interactorIdB = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomInteraction that = (CustomInteraction) o;

        if (interactorIdA != null ? !interactorIdA.equals(that.interactorIdA) : that.interactorIdA != null)
            return false;
        return !(interactorIdB != null ? !interactorIdB.equals(that.interactorIdB) : that.interactorIdB != null);

    }

    @Override
    public int hashCode() {
        int result = interactorIdA != null ? interactorIdA.hashCode() : 0;
        result = 31 * result + (interactorIdB != null ? interactorIdB.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CustomInteraction{" +
                "interactorIdA='" + interactorIdA + '\'' +
                ", interactorIdB='" + interactorIdB + '\'' +
                ", alternativeInteractorA='" + alternativeInteractorA + '\'' +
                ", alternativeInteractorB='" + alternativeInteractorB + '\'' +
                ", aliasInteractorA='" + aliasInteractorA + '\'' +
                ", aliasInteractorB='" + aliasInteractorB + '\'' +
                ", interactionDetectionMethod='" + interactionDetectionMethod + '\'' +
                ", publication1stAuthor='" + publication1stAuthor + '\'' +
                ", publicationIdentifier='" + publicationIdentifier + '\'' +
                ", taxonomyIdInteractorA='" + taxonomyIdInteractorA + '\'' +
                ", taxonomyIdInteractorB='" + taxonomyIdInteractorB + '\'' +
                ", interactionType='" + interactionType + '\'' +
                ", sourceDatabase='" + sourceDatabase + '\'' +
                ", interactionIdentifier='" + interactionIdentifier + '\'' +
                ", confidenceValue='" + confidenceValue + '\'' +
                '}';
    }
}
