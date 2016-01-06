package org.reactome.server.tool.interactors.dao;


import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractorDAO;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestPsicquic {

    private InteractorDAO interactorDAO;

    @Before
    public void setUp() {
        interactorDAO = DAOFactory.createInteractorDAO();
    }


    @Test
    public void testInteractorDAO() {
//        try {
//
//            List<String> proteinOrChemicalList = new ArrayList<String>();
//            proteinOrChemicalList.add("Q13501");
//
//            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();
//
//            List<ServiceType> services = registryClient.listActiveServices();
//
//            InteractorResource interactorResource = new InteractorResource();
//            interactorResource.setId(1000L);
//            interactorResource.setName("UniProt");
//            interactorResource.setUrl("http://www.uniprot.org/query/##ID##");
//
//            InteractionResource interactionResource = new InteractionResource();
//            interactionResource.setId(2000L);
//            interactionResource.setName("IntAct");
//            interactionResource.setUrl("http://www.ebi.ac.uk/intact/interaction/##ID##");
//
//            // http://www.uniprot.org/uniprot/Q14145#interaction
//            // http://www.uniprot.org/uniprot/Q13501#interaction
//
//            /** For each Psicquic service - query proteins **/
//            for (ServiceType service : services) {
//
//                if(service.getName().equalsIgnoreCase("intact")) {
//                    for (String proteinOrChemical : proteinOrChemicalList) {
//
//                        List<BinaryInteraction> allBinaryInteraction = new ArrayList<BinaryInteraction>();
//
//                        UniversalPsicquicClient client = new UniversalPsicquicClient(service.getSoapUrl());
//
//                        MitabSearchResult result = client.getByInteractor(proteinOrChemical, 0, 500);
//
//                        allBinaryInteraction.addAll(result.getData());
//
//                        Interactor interactor = new Interactor();
//                        interactor.setInteractorResourceId(1000L);
//                        interactor.setAcc(proteinOrChemical);
//
//                        for (BinaryInteraction binaryInteraction : allBinaryInteraction) {
//                            String interactorIdA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier();
//                            String interactorIdB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();
//
//                            InteractionDetails interaction = new InteractionDetails();
//
//                            //interaction.setInteractorA();
//                            //interaction.setInteractorB();
//                            if(binaryInteraction.getSourceDatabases().size() > 0) {
//                                CrossReference sourceDatabase = (CrossReference) binaryInteraction.getSourceDatabases().iterator().next();
//                                System.out.println(sourceDatabase.getText());
//                            }
//
//                            if (binaryInteraction.getInteractionAcs().size() > 0) {
//                                CrossReference cr = (CrossReference) binaryInteraction.getInteractionAcs().iterator().next();
//                                String interactionAc = cr.getIdentifier();
//
//
//                               // interaction.setInteractionId(interactionAc);
//
//                                //System.out.println("Interaction (" + interactionAc + "): " + interactorIdA + " interacts with " + interactorIdB);
//
//                            }
//
//                            String score = "";
//                            if (binaryInteraction.getConfidenceValues().size() > 0) {
//                                //System.out.println("\t\t"+binaryInteraction.getConfidenceValues());
//
//                                Confidence confidence = (Confidence) binaryInteraction.getConfidenceValues().iterator().next();
//
//                                score = confidence.getValue();
//
//
//                                //System.out.println("Interaction (" + interactionAc + "): " + interactorIdA + " interacts with " + interactorIdB);
//
//                            }
//
//                            System.out.println("Interaction (" + interaction.getInteractionId() + "): " + interactorIdA + " interacts with " + interactorIdB + " -- Score: " + score);
//
//
//                        }
//                        //boolean created = interactorDAO.create(interactor);
//
//                        //assertTrue(created);
//
//                        //interactorID = interactor.getId();
//
//                        /** Get from DB **/
//                        //Interactor dbInteractor = interactorDAO.getById(interactorID);
//                        //assertNotNull(dbInteractor);
//                        //assertEquals(dbInteractor.getId(), interactorID);
//
//                        //boolean deleted = interactorDAO.delete(interactorID);
//
//                        //assertTrue("Could not delete the Interactor.", deleted);
//                    }
//                }
//            }
//        } catch (PsicquicRegistryClientException e) {
//            fail(e.getMessage());
//        } catch (PsicquicClientException e) {
//            fail(e.getMessage());
//        }
    }

}
