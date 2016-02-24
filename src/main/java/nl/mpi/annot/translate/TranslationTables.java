/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author petbei
 */
public class TranslationTables {
    private ArrayList<TranslationTable> translationTableArray = null;
    private HashMap<String, Integer> translationTableMap = null;
    
    public TranslationTables() {
        //System.err.println("--- LOG --- created \"TranslationTables\" object");
        translationTableArray = new ArrayList<TranslationTable>();
        translationTableMap = new HashMap<String, Integer>();
    }
    
    TranslationTable addTranslationTable(ResourceLayer fromRL, ResourceLayer toRL, TranslationType transType) throws ATException {
        //--- check validity of parameters
        if (fromRL == null) {
            System.out.println("*** ERROR *** TranslationTables: attempt to add NULL 'from' ResourceLayer name - skipped");
            throw new ATException("definition of TranslationTable: null 'from' ResourceLayer");
        }
        if (toRL == null) {
            System.out.println("*** ERROR *** TranslationTables: attempt to add NULL 'to' ResourceLayer name - skipped");
            throw new ATException("definition of TranslationTable: null 'to' ResourceLayer");
        }
        if (transType == null) {
            System.out.println("*** ERROR *** TranslationTables: attempt to add NULL or empty translation type object - skipped");
            throw new ATException("definition of translationTable: null translation type");
        }
        
        //--- check if Resource/Layer combination is already defined
        if (fromRL.getName().equals(toRL.getName())) {
            System.out.println("*** ERROR *** new TranslationTable: 'from' and 'to' Resource/Layers are the same - ignored");
            throw new ATException("definition of translationTable: 'from' and 'to' Resource/Layers are the same");
        }
        String transTabKey = fromRL.getName() + "-" + toRL.getName() + "=" + transType.getId();
        if (translationTableMap.containsKey(transTabKey)) {
            System.out.println("*** ERROR *** TranslationTable \"" + transTabKey + "\" was already defined - ignored");
            throw new ATException("definition of translationTable: '" + transTabKey + "' was already defined");            
        }
        //--- not defined; create new one
        TranslationTable nw = new TranslationTable(transTabKey, fromRL, toRL, transType);
        translationTableArray.add(nw);
        translationTableMap.put(transTabKey, translationTableArray.size() - 1);
        return nw;
    }

    public List<TranslationTable> getList() {
	return translationTableArray;
    }
    
    TranslationTable find(String fromRes, String fromLay, String toRes, String toLay, TranslationType ty) {
        String fromResLayId = fromRes + "/" + fromLay;
        String toResLayId = toRes + "/" + toLay;
        String tableId = fromResLayId + "-" + toResLayId + "=" + ty.getId();
        if (translationTableMap.containsKey(tableId)) {
            return translationTableArray.get(translationTableMap.get(tableId));
        }
        else {
            System.err.println("*** ERROR *** failed to find translation table " + tableId);
            return null;
        }
    }
    
    void display() {
        System.out.println("========== START list of TranslationTables ==========");
        for (TranslationTable tt: translationTableArray) {
            tt.display();
        }
        System.out.println("========== END list of TranslationTables ==========\n");
    }
}
