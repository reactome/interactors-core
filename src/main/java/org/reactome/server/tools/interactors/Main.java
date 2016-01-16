package org.reactome.server.tools.interactors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
    }

//    public static void main(String[] args) {
//        List<String> reactomeProteins = new ArrayList<String>();
////        reactomeProteins.add("bbc3");
//        reactomeProteins.add("Q06609");
//
//        try {
//            /**
//             * Create Resources
//             */
//            List<ServiceType> psicquicServices = getPsicquicResources();
//
//            int maxResults = 10;
//
//            for (String reactomeProtein : reactomeProteins) { // TODO Try querying by list to optimize
//
//                logger.info("Querying PROTEIN: " + reactomeProtein);
//
//                for (ServiceType service : psicquicServices) {
//
//                    int firstResult = 0;
//                    logger.info("Service: " + service.getName());
//
//                    List<BinaryInteraction> allBinaryInteraction = new ArrayList<BinaryInteraction>();
//
//                    UniversalPsicquicClient client = new UniversalPsicquicClient(service.getSoapUrl());
//
//                    MitabSearchResult result = client.getByQuery(reactomeProtein, firstResult, maxResults);
//
//                    allBinaryInteraction.addAll(result.getData());
//
//                    int totalResults = result.getTotalCount();
//                    logger.info("Total: " + totalResults);
//                    int runs = totalResults / maxResults;
//
//                    logger.info(runs+"");
//                    int i = 0;
//
//                    while(i <= runs){
//                        logger.info(i +"");
//
//                        firstResult = firstResult + maxResults;
//
//                        logger.info("FirstResult: " + firstResult);
//                        result = client.getByQuery(reactomeProtein, firstResult, maxResults);
//
//                        allBinaryInteraction.addAll(result.getData());
//
//                        i++;
//                    }
//
//                    logger.info("ALL ITERACTION " + allBinaryInteraction.size()+"");
//
//                    for (BinaryInteraction binaryInteraction : allBinaryInteraction) {
//                        String interactorIdA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier();
//                        String interactorIdB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();
//
//
//
//                        if (binaryInteraction.getInteractionAcs().size() > 0) {
//                            CrossReference cr = (CrossReference) binaryInteraction.getInteractionAcs().iterator().next();
//                            String interactionAc = cr.getIdentifier();
//
//                            logger.info("Interaction (" + interactionAc + "): " + interactorIdA + " interacts with " + interactorIdB);
//
//                        } else {
//
//                        }
//
//
//                    }
//
//
//
//                }
//            }
//
//
//
//
//
//
//
//
//
//        } catch (PsicquicRegistryClientException e) {
//            e.printStackTrace();
//        } catch (PsicquicClientException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Get Psicquic Registry servers
//     *
//     * @return
//     * @throws PsicquicRegistryClientException
//     */
//    public static List<ServiceType> getPsicquicResources() throws PsicquicRegistryClientException {
//        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();
//
//        return registryClient.listServices();
//    }

}
