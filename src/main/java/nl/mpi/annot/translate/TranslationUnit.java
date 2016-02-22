/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author petbei
 */
public class TranslationUnit {
    private String fromStr;
    private HashMap<String, Integer> toMap;
    
    public TranslationUnit(String f) {
        fromStr = f;
        toMap = new HashMap<String, Integer>();
    }
    
    boolean haveToString(String s) {
        return (toMap.containsKey(s));
    }
    
    void addTranslation(String s) {
        if (!haveToString(s)) {
            toMap.put(s, 1);
        }
    }
    
    ArrayList<String> getTranslationValues() {
        return new ArrayList<String>(toMap.keySet());
    }
    
    void display() {
        for (String value: toMap.keySet()) {
            System.out.println("    \"" + fromStr + "\" => \"" + value + "\"");
        }
    }
}
