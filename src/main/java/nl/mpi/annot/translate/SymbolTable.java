/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author petbei
 */
public class SymbolTable {
    Map<String, Integer> stMap = new ConcurrentHashMap<String, Integer>();
    
    int getSize() {
        return stMap.size();
    }
    
    void add(String s) {
        // precondition: 's' is not null. Empty String value ("") is allowed
        if (stMap.containsKey(s)) {
            stMap.put(s, stMap.get(s) + 1);
            // System.out.println("SymbolTable.addSymbol(s): symbol \"" + s + "\" is already defined");
        }
        else {
            // System.out.println("SymbolTable.addSymbol(s): new symbol \"" + s + "\" added");
            stMap.put(s, 1);
        }
    }
    
    boolean isDefined(String s) {
        // precondition: 's' is not null. Empty String value ("") is allowed
        return (stMap.containsKey(s));
    }
    
    Integer get(String s) {
        // precondition: 's' is not null. Empty String value ("") is allowed
        if (stMap.containsKey(s)) {
            //System.out.println("SymbolTable.isDefined(s): symbol \"" + s + "\" is defined");
            return stMap.get(s);
        }
        else {
            //System.out.println("SymbolTable.isDefined(s): symbol \"" + s + "\" is NOT defined");
            return null;
        }
    }
    
    void display() {
        System.out.println("========== START symbol table: " + stMap.size() + " elements ==========");
        for (String key: stMap.keySet()) {
            System.out.println("    \"" + key + "\" -> " + stMap.get(key));
        }      
        System.out.println("========== END symbol table " + " ==========\n");
    }
}
