/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author petbei
 */
public class TranslationTypes {
    private ArrayList<TranslationType> ttArray = new ArrayList<TranslationType>();
    private HashMap<String, Integer> ttMap = new HashMap<String, Integer>();
    private static int errorCount = 0;
    
    public TranslationType ErrorTranslationType;
    
    public TranslationTypes() {
        ErrorTranslationType = new TranslationType("***_ERROR-AT-RESOURCE_***");
    }
    
    TranslationType addTranslationType(String id) {
        if (id == (String) null || id.length() <= 0) {
            errorCount++;
            System.out.println("*** ERROR *** TranslationTypes: attempt to define unique Formalism with illegal-value (null or empty) identifier");
            return ErrorTranslationType;
        }
        if (ttMap.containsKey(id)) {
            return ttArray.get(ttMap.get(id));
        }
        TranslationType nw = new TranslationType(id);
        ttArray.add(nw);
        ttMap.put(id, ttArray.size() - 1);
        return nw;
    }
    
    boolean haveTranslationType(String id) {
        if (id == (String) null || id.length() <= 0) {
            return false;
        }
        return (ttMap.containsKey(id));
    }
    
    TranslationType getTranslationType(String id) {
        if (id == (String) null || id.length() <= 0) {
            return null;
        }
        if (ttMap.containsKey(id)) {
            return ttArray.get(ttMap.get(id));
        }
        else {
            return null;
        }
    }
    
    int getSize() {
        return ttArray.size();
    }
    
    void display() {
        System.out.println("========== START TranslationTypes: " + ttArray.size() + " ==========");
        for (TranslationType tt: ttArray) {
            tt.display();
        }
        System.out.println("========== END TranslationTypes: " + ttArray.size() + " ==========\n");
    } 
}
