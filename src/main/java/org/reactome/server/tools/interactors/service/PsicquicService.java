package org.reactome.server.tools.interactors.service;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.reactome.server.tools.interactors.dao.PsicquicDAO;
import org.reactome.server.tools.interactors.dao.psicquic.InteractionImpl;
import org.reactome.server.tools.interactors.model.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class PsicquicService {

    PsicquicDAO psicquicDAO;

    public PsicquicService(){
       psicquicDAO  = new InteractionImpl();
    }

    public List<Interaction> getInteractions(String resource, String acc){
        return psicquicDAO.getInteraction(resource, acc);
    }

    public Map<String, List<Interaction>> getInteractions(String resource, Collection<String> accs){
        return psicquicDAO.getInteraction(resource, accs);
    }

    public List<PsicquicRegistry> getRegistries(){
        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        List<PsicquicRegistry> registries = new ArrayList<>();

        try {
            List<ServiceType> services = registryClient.listServices();
            for (ServiceType service : services) {
                PsicquicRegistry p = new PsicquicRegistry();
                p.setActive(service.isActive());
                p.setName(service.getName());
                p.setRestURL(service.getRestUrl());
                p.setSoapURL(service.getSoapUrl());
                registries.add(p);
            }


        } catch (PsicquicRegistryClientException e) {
            e.printStackTrace();
        }
        return  registries;

    }

}
