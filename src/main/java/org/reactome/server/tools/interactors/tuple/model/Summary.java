package org.reactome.server.tools.interactors.tuple.model;

import org.reactome.server.tools.interactors.tuple.token.Token;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class Summary {

    private Token token;

    private UserDataContainer data;

    public Summary(Token token, UserDataContainer data) {
        this.token = token;
        this.data = data;
    }

    private List<String> warningMessages;
    private List<String> errorMessages; // maybe the error error message goes in the Exception
    private List<String> headerColumns;
    private Integer numberOfInteractors;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public UserDataContainer getData() {
        return data;
    }

    public void setData(UserDataContainer data) {
        this.data = data;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(List<String> warningMessages) {
        this.warningMessages = warningMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<String> getHeaderColumns() {
        return headerColumns;
    }

    public void setHeaderColumns(List<String> headerColumns) {
        this.headerColumns = headerColumns;
    }

    public Integer getNumberOfInteractors() {
        return numberOfInteractors;
    }

    public void setNumberOfInteractors(Integer numberOfInteractors) {
        this.numberOfInteractors = numberOfInteractors;
    }
}
