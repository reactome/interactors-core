package org.reactome.server.tools.interactors.tuple.token;

import java.util.Date;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
@Deprecated
public class Token {

    private String id;
    private Date createDate = new Date();

    public Token(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                ", createDate=" + createDate +
                '}';
    }
}
