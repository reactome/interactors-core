package org.reactome.server.interactors.tuple.parser.response;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that builds the Response.
 * @author Guilherme Viteri <gviteri@ebi.ac.uk>
 */
public class Response {

    /**
     * Codes
     */
    public final static Integer UNEXPECTED_ERROR = 450;

    public final static Integer NO_HEADER_WARNING = 451;
    public final static Integer EMPTY_FILE = 452;
    public final static Integer NO_HEADER_ERROR = 453;
    public final static Integer INLINE_PROBLEM = 454;
    public final static Integer EMPTY_LINE = 455;
    public final static Integer POTENTIAL_HEADER = 456;
    public final static Integer COLUMN_MISMATCH = 457;
    public final static Integer DUPLICATE_AB = 458;
    public final static Integer DUPLICATE_BA = 459;
    public final static Integer MISSING_MANDATORY_FIELDS = 460;
    public final static Integer AVOIDED_SCORE = 461;



    /**
     * messages - Strings
     */
    private final static String MESSAGE_UNEXPECTED_ERROR = "Unexpected Error";
    private final static String MESSAGE_NO_HEADER_WARNING = "Missing header. Using a default one.";
    private final static String MESSAGE_EMPTY_FILE = "There is no interactor file to be parsed.";
    private final static String MESSAGE_NO_HEADER_ERROR = "Missing header. Cannot parse your file properly";
    private final static String MESSAGE_INLINE_PROBLEM = "Line {0} has been removed. Invalid value found on Column {1}.";
    private final static String MESSAGE_EMPTY_LINE = "Line {0} is empty and has been ignored.";
    private final static String MESSAGE_POTENTIAL_HEADER = "The first line seems to be a header. Make sure it is being initialised by # or //.";
    private final static String MESSAGE_COLUMN_MISMATCH = "Line {0} does not have {1} column(s). {2} Column(s) found.";
    private final static String MESSAGE_DUPLICATE_AB = "Line {0} has duplicated interaction. Accession A: {1} and Accession B: {2}";
    private final static String MESSAGE_DUPLICATE_BA = "Line {0} has duplicated interaction. Accession A: {1} and B: {2}, match existing A: {3} and B: {4}";
    private final static String MESSAGE_MISSING_MANDATORY_FIELDS = "Line {0} does not have mandatory field(s): {1}";
    private final static String MESSAGE_AVOIDED_SCORE = "{0} entries have been omitted. Their score is less than {1}";

    /**
     * handles Error codes to Message strings
     */
    public final static Map<Integer, String> codeToMessage;

    static {
        // change initial size if as new messages has been added.
        codeToMessage = new HashMap<>(11);

        codeToMessage.put(UNEXPECTED_ERROR, MESSAGE_UNEXPECTED_ERROR);

        codeToMessage.put(NO_HEADER_WARNING, MESSAGE_NO_HEADER_WARNING);
        codeToMessage.put(EMPTY_FILE, MESSAGE_EMPTY_FILE);
        codeToMessage.put(INLINE_PROBLEM, MESSAGE_INLINE_PROBLEM);
        codeToMessage.put(EMPTY_LINE, MESSAGE_EMPTY_LINE);
        codeToMessage.put(NO_HEADER_ERROR, MESSAGE_NO_HEADER_ERROR);
        codeToMessage.put(POTENTIAL_HEADER, MESSAGE_POTENTIAL_HEADER);
        codeToMessage.put(COLUMN_MISMATCH, MESSAGE_COLUMN_MISMATCH);
        codeToMessage.put(DUPLICATE_AB, MESSAGE_DUPLICATE_AB);
        codeToMessage.put(DUPLICATE_BA, MESSAGE_DUPLICATE_BA);
        codeToMessage.put(MISSING_MANDATORY_FIELDS, MESSAGE_MISSING_MANDATORY_FIELDS);
        codeToMessage.put(AVOIDED_SCORE, MESSAGE_AVOIDED_SCORE);

    }

    /**
     * Return appropriate message based on the code
     * @param code integer code message
     * @return message
     */
    public static String getMessage(Integer code) {
        return codeToMessage.get(code);
    }


    /**
     * Returns error message based on param int code after formatting message
     * with args
     * @param code integer code message
     * @param args parameter argument
     * @return message
     */
    public static String getMessage(Integer code, Object... args) {

        String message = Response.getMessage(code);

        if ((args != null) && (args.length > 0)) {
            MessageFormat format = new MessageFormat(message);
            message = format.format(args);
        }

        return message;
    }

    /**
     * Returns error message based on param int code after formatting message
     * with arg
     * @param code integer code message
     * @param arg parameter argument
     * @return message
     */
    public static String getMessage(Integer code, int arg) {

        String message = Response.getMessage(code);

        MessageFormat format = new MessageFormat(message);
        message = format.format(new Object[] { arg });

        return message;
    }
}