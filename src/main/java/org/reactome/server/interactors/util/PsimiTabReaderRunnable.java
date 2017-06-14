package org.reactome.server.interactors.util;

import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class PsimiTabReaderRunnable implements Runnable {

    private String acc;
    private URL url;
    private ConcurrentHashMap<String, List<BinaryInteraction>> accInteractionsMap;

    public PsimiTabReaderRunnable(String acc, URL url, ConcurrentHashMap<String, List<BinaryInteraction>> accInteractionsMap) {
        this.acc = acc;
        this.url = url;
        this.accInteractionsMap = accInteractionsMap;
    }

    public void run() {
        List<BinaryInteraction> binaryInteractionList = new ArrayList<>();
        try {
            PsimiTabReader mitabReader = new PsimiTabReader();

            binaryInteractionList.addAll(mitabReader.read(url));
            synchronized (this) {
                accInteractionsMap.put(acc, binaryInteractionList);
            }
        } catch (Exception e) {
            // TODO Show a message, or log sth.
        }
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
