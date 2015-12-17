package org.reactome.server.tools.interactors.util;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.wsclient.PsicquicClientException;
import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import org.hupo.psi.mi.psicquic.wsclient.result.MitabSearchResult;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.Confidence;
import psidev.psi.mi.tab.model.CrossReference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme Segantin Viteri <gviteri@ebi.ac.uk>
 */

public class PsicquicSingleQuery {


    public static void main(String[] args){
        String query = "Q13501";
        int firstResult = 0;
        int maxResult = 100;

        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        List<ServiceType> services = null;
        try {
            services = registryClient.listActiveServices();
        } catch (PsicquicRegistryClientException e) {
            e.printStackTrace();
        }


        for (ServiceType service : services) {
            List<BinaryInteraction> all = new ArrayList<BinaryInteraction>();

            System.out.println("Querying: " + service.getName() + " [" + service.getSoapUrl() + "]");

            try {
            UniversalPsicquicClient client = new UniversalPsicquicClient(service.getSoapUrl());
            client.setConnectionTimeOut(10000L);

            MitabSearchResult result = client.getByQuery(query, firstResult, 100);

            all.addAll(result.getData());

            System.out.println("Interactions found: " + result.getTotalCount());
            //System.out.println("Interactions DATA: " + result.getData().size());

            for (BinaryInteraction binaryInteraction : result.getData()) {
                String interactorIdA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier();
                String interactorIdB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();

                if (binaryInteraction.getInteractionAcs().size() > 0) {
                    CrossReference cr = (CrossReference) binaryInteraction.getInteractionAcs().iterator().next();
                    String interactionAc = cr.getIdentifier();

                    String scoreTxt = null;
                    if(binaryInteraction.getConfidenceValues().size() > 0){
                        Confidence score = (Confidence)binaryInteraction.getConfidenceValues().iterator().next();
                        scoreTxt = score.getValue();
                    }


                    System.out.println("\tInteraction (" + interactionAc + "): " + interactorIdA + " interacts with " + interactorIdB + " -- Score: " + scoreTxt );

                } else {
                    System.out.println("Null ?");
                }
            }

            }catch (PsicquicClientException e){
                System.out.println(" can't query this service ... ");
            }

            System.out.println("---------------------------------------\n");
        }

    }
}


