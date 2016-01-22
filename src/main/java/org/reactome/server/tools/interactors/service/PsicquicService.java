package org.reactome.server.tools.interactors.service;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.reactome.server.tools.interactors.dao.PsicquicDAO;
import org.reactome.server.tools.interactors.dao.psicquic.InteractionClusterImpl;
import org.reactome.server.tools.interactors.model.*;

import java.util.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class PsicquicService {

    PsicquicDAO psicquicDAO;

    public PsicquicService(){
       psicquicDAO  = new InteractionClusterImpl();
    }

    public List<Interaction> getInteractions(String resource, String acc){
        return psicquicDAO.getInteraction(resource, acc);
    }

    public Map<String, List<Interaction>> getInteractions(String resource, Collection<String> accs){
        return psicquicDAO.getInteraction(resource, accs);
    }

    public List<PsicquicResource> getResources(){
        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        List<PsicquicResource> resourceList = new ArrayList<>();

        try {
            List<ServiceType> services = registryClient.listServices();
            for (ServiceType service : services) {
                PsicquicResource p = new PsicquicResource();
                p.setActive(service.isActive());
                p.setName(service.getName());
                p.setRestURL(service.getRestUrl());
                p.setSoapURL(service.getSoapUrl());

                resourceList.add(p);
            }


        } catch (PsicquicRegistryClientException e) {
            e.printStackTrace();
        }

        Collections.sort(resourceList);
        return  resourceList;

    }

}
