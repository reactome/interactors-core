package org.reactome.server.tools.interactors.tuple.token;

import java.util.Date;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class Token {

    private String id;
    private String sampleName;
    private Date createDate = new Date();
    private Date updateDate = new Date();
    private boolean valid = true;

    public Token (String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        return !(id != null ? !id.equals(token.id) : token.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id='" + id + '\'' +
                ", sampleName='" + sampleName + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", valid=" + valid +
                '}';
    }
}
