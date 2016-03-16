package org.reactome.server.tools.interactors.tuple.parser;

import org.reactome.server.tools.interactors.tuple.exception.ParserException;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class ParserFactory {

    public enum FileDefinition {
        REDUCED_DATA(2),
        EXTENDED_DATA(15),
        PSIMITAB_DATA(15);

        final int columns;

        FileDefinition(int columns) {
            this.columns = columns;
        }

    }

    public static Parser build(List<String> lines) throws ParserException {
        Parser parser;

        FileDefinition fileDefinition = getFileDefinition(lines);
        switch (fileDefinition) {
            case REDUCED_DATA:
                parser = new TupleParser();
                break;
            case EXTENDED_DATA:
                parser = new ExtendedParser();
                break;
            case PSIMITAB_DATA:
                parser = new PsimiTabParser();
                break;
            default:
                parser = null;
                break;
        }

        return parser;
    }

    private static FileDefinition getFileDefinition(List<String> lines) throws ParserException {

        // some logic here to get the file definition ... check number of columns.

        // the easiest is try to parse the psimitab and get the exception

        String header = lines.get(0);
        if (header.startsWith("#") || header.startsWith("//")) {
            header = header.replaceAll("^(#|//)", "");

            String[] cols = header.split("[\\t,;]+");

            if (cols.length == 2) {
                return FileDefinition.REDUCED_DATA;
            }

            return FileDefinition.EXTENDED_DATA;
        }

        return FileDefinition.PSIMITAB_DATA;
    }
}
