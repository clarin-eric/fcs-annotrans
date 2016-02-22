/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

/**
 *
 * @author petbei
 */
public class TranslationType {
    private String translationTypeId;
    private static int errorCount = 0;
    
    public TranslationType(String id) {
        if (id == (String) null || id.length() <= 0) {
            errorCount++;
            System.err.println("*** ERROR *** attempt to define TranslationType with null or empty identifier");
            translationTypeId = "ERROR_TranslationTypeId_" + errorCount;
        }
        else {
            translationTypeId = id;
        }
    }
    
    String getId() {
        return translationTypeId;
    }
    
    int getErrorCount() {
        return errorCount;
    }
    
    void display() {
        System.out.println("    TranslationType: \"" + translationTypeId + "\"");
    }    
}
