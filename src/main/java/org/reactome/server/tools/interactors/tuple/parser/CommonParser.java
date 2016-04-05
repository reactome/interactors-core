package org.reactome.server.tools.interactors.tuple.parser;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.reactome.server.tools.interactors.tuple.model.ColumnDefinition;
import org.reactome.server.tools.interactors.tuple.model.CustomInteraction;
import org.reactome.server.tools.interactors.tuple.model.UserDataContainer;
import org.reactome.server.tools.interactors.tuple.util.FileDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This is what is common between our parsers.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public abstract class CommonParser implements Parser {

    protected static Logger logger = Logger.getLogger(CommonParser.class);

    protected List<String> errorResponses = new LinkedList<>();
    protected List<String> warningResponses = new LinkedList<>();

    protected boolean hasHeaderLine(String line) {
        return line.startsWith("#") || line.startsWith("//");
    }

    /**
     * An easy handy method for determining if the parse succeeded
     *
     * @return true if data is wrong, false otherwise
     */
    protected boolean hasError() {
        return errorResponses.size() >= 1;
    }

    /**
     * Retrieve the parser definition based on the specific implementation.
     * Each implementation of CommonParser has its own mechanism to identify the file definition
     *
     * @param lines is the content to be analysed
     * @return the FileDefinition
     */
    public abstract FileDefinition getParserDefinition(List<String> lines);

    /**
     * Generic method that invoke via Reflections the methods in the CustomInteraction class
     * and check if the attribute has value on it.
     *
     * @param customInteraction to check mandatory fields based on the ColumnDefinition enum
     *
     * @return List of mandatory fields that are empty.
     */
    protected List<String> checkMandatoryAttributes(CustomInteraction customInteraction) {
        List<String> mandatoryList = new ArrayList<>();

        List<ColumnDefinition> mand = ColumnDefinition.getMandatoryColumns();
        for (ColumnDefinition columnDefinition : mand) {
            try {
                String getter = "get".concat(StringUtils.capitalize(columnDefinition.attribute));
                Method method = customInteraction.getClass().getMethod(getter);
                Object returnValue = method.invoke(customInteraction);

                if (method.getReturnType().equals(String.class)) {
                    String returnValueStr = (String) returnValue;
                    if (StringUtils.isBlank(returnValueStr)) {
                        mandatoryList.add(columnDefinition.name());
                    }
                }

                if (method.getReturnType().equals(List.class)) {
                    List<String> list = (List<String>) returnValue;
                    if (list.size() == 0) {
                        mandatoryList.add(columnDefinition.name());
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return mandatoryList;

    }

    protected int countInteractors(UserDataContainer userDataContainer) {
        Set<String> interactors = new HashSet<>(userDataContainer.getCustomInteractions().size());

        for (CustomInteraction interaction : userDataContainer.getCustomInteractions()) {
            // add interactors into a set in order to count them as unique.
            interactors.add(interaction.getInteractorIdA());
            interactors.add(interaction.getInteractorIdB());

        }

        return interactors.size();

    }

}