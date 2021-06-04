package org.reactome.server.interactors.tuple.parser;

import org.reactome.server.interactors.tuple.exception.ParserException;
import org.reactome.server.interactors.tuple.util.FileDefinition;
import org.reactome.server.interactors.util.Toolbox;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class ParserFactory {

    /** Package where our parses reside **/
    private static final String PARSER_PACKAGE = "org.reactome.server.interactors.tuple.parser";

    /**
     * Build the parser based on the file content that is analysed in order to return the proper instance
     *
     * @return the specific parser
     * @throws ParserException
     */
    public static Parser build(List<String> lines) throws ParserException {
        Parser parser;

        FileDefinition fileDefinition = getParserDefinition(lines);

        if (fileDefinition == null) {
            throw new ParserException("Could not Parse your file");
        }

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

    /**
     * Scan all parsers that are extending CommonParser
     *
     * @param lines content file to be analysed
     * @return the File Definition Implementation
     * @throws ParserException
     */
    private static FileDefinition getParserDefinition(List<String> lines) throws ParserException {
        try {
            Set<Class<? extends CommonParser>> parsers = Toolbox.getSubTypesOf(PARSER_PACKAGE, CommonParser.class);
            for (Class aClass : parsers) {
                CommonParser commonParser = (CommonParser) aClass.getDeclaredConstructor().newInstance();

                FileDefinition fileDef = commonParser.getParserDefinition(lines);
                if (fileDef != null) {
                    return fileDef;
                }
            }
        } catch (InstantiationException e) {
            throw new ParserException("Couldn't instantiate the parser which identifies the format", e);
        } catch (IllegalAccessException e) {
            throw new ParserException("Illegal access to the parser. Couldn't identify the format", e);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}
