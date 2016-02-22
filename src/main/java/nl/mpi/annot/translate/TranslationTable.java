/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author petbei
 */
public class TranslationTable {
    private String                tableId;        //--- table identifier; unique
    private ResourceLayer         fromRL;         //--- unique resource/layer combination, defines format
    private ResourceLayer         toRL;           //--- unique resource/layer combination, defines format
    private TranslationType       transType;      //--- definition of algoritm that defines how to translate 'from' to 'to'
    Map<String, TranslationUnit>  translations;   //--- map of all 'from' translation
    private Trellis               trellis;        //--- trellis data structure to handle segmented (phonetic) translations
    
    public TranslationTable(String tId, ResourceLayer f, ResourceLayer t, TranslationType tt) {
        tableId = tId;  //--- unique ID: fromRL.id + "-" + toRL
        fromRL = f;     //--- Resource/Layer combination
        toRL = t;       //--- Resource/Layer combination
        transType = tt; //--- translation method indication
        translations = new ConcurrentHashMap<String, TranslationUnit>();
        trellis = null;
    }
    
    Trellis getTrellis() {
        return trellis;
    }
    
    void tryPopulateTrellis() {
        if (!(transType.getId().equals("replaceSegments"))) {
            // System.err.println("INFO: TranslationTable " + tableId + " is not converted to Trellis, wrong TranslationType (" + transType.getId() + ")");
            return;
        }
        if (translations.size() <= 0) {
            System.err.println("INFO: TranslationTable " + tableId + " is not converted to Trellis, no translation data");
            return;
        }
        trellis = new Trellis();
        for (String fStr: translations.keySet()) {
            TranslationUnit tu = translations.get(fStr);
            for (String tStr: tu.getTranslationValues()) {
                //System.out.println("TranslationTable " + tableId + ": add trellis pair(" + fStr + "," + tStr+ ")");
                trellis.addTranslationPair(fStr, tStr);
            }
        }
    }
    
    void addTranslation(String f, String t) {
        TranslationUnit tp;
        if (translations.containsKey(f)) {
            tp = translations.get(f);
        }
        else {
            tp = new TranslationUnit(f);
            translations.put(f, tp);
        }
        tp.addTranslation(t);
    }
    
    TranslationResult translate(String fromValue) {
        if (transType.getId().equals("replaceWhole")) {
            TranslationResult result = new TranslationResult(fromValue, fromRL.getFormalism());
            result.setOutFormat(toRL.getFormalism());
            if (translations.containsKey(fromValue)) {
                TranslationUnit tu = translations.get(fromValue);
                result.setOutTexts(tu.getTranslationValues());
            }
            return result;
        }
        if (transType.getId().equals("replaceSegments")) {
            TranslationResult result = new TranslationResult(fromValue, fromRL.getFormalism());
            result.setOutFormat(toRL.getFormalism());
            result.setOutTexts(trellis.translate(fromValue));
            return result;
        }
        System.err.println("Can't translate " + fromValue + ", unknown translation type: " + transType.getId());
        return (TranslationResult) null;
    }
    
    void display() {
        System.out.println("  from " + fromRL.getName() + "(" + fromRL.getFormalism() + ") to " + toRL.getName() + "(" + toRL.getFormalism() + ") => " + transType.getId());
        for (String key: translations.keySet()) {
            translations.get(key).display();
        }
    }
}
