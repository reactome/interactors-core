package org.reactome.server.tool.interactors.dao;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestPsicquicRegistry {

    @Test
    public void testPsicquicRegistry() {
        try {
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();
            List<ServiceType> services = registryClient.listServices();

            for (ServiceType service : services) {
                assertNotNull(service);
            }

        }catch(PsicquicRegistryClientException e){
            fail();
        }
    }
}
