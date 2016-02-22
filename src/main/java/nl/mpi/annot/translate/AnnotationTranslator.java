/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.util.ArrayList;

/**
 *
 * @author petbei
 * 
 * 
 */
public class AnnotationTranslator {
        
    class TranslationPair {
        String fromStr;
        String toStr;
        TranslationPair(String f, String t) {
            fromStr = f;
            toStr = t;
        }
    }
    
    //enum TranslationType {
    //    TT_undefined,
    //    TT_replaceLiteral,
    //    TT_attributedReplace,
    //    TT_replacePerSegment,
    //    TT_replacePerSegmentAndSpellcheck
    //}
    
    SymbolTable       resources;          //--- unique set of resources
    SymbolTable       layers;             //--- unique set of layers/views
    SymbolTable       formalisms;         //--- unique set of linguistic encoding formalisms (UD17, CGN PoS, X-Sampa, IPA, CG sampa, ...)
    TranslationTypes  translationTypes;   //--- method to translate a String in formalism A to Formalism B
    ResourceLayers    resourceLayers;     //--- unique resource/layer combinations
    TranslationTables translationTables;  //--- unique translation tables

    public AnnotationTranslator(String xmlUri) {
        resources           = new SymbolTable();
        layers              = new SymbolTable();
        formalisms          = new SymbolTable();
        translationTypes    = new TranslationTypes();
        resourceLayers      = new ResourceLayers();
        translationTables   = new TranslationTables();
        XMLparser xp = new XMLparser(xmlUri, resources, layers, formalisms, translationTypes, resourceLayers, translationTables);
    }
    
    TranslationType getTranslationType(String ttId) {
        return translationTypes.getTranslationType(ttId);
    }
    
    TranslationTable findTranslationTable(String fromRes, String fromLay, String toRes, String toLay, TranslationType ty) {
        return translationTables.find(fromRes, fromLay, toRes, toLay, ty);
    }
    
    public TranslationResult translateAnnotation(String tabId, String inStr) {
        System.err.println("*** WARNING *** not implemented yet: \"AnnotationTranslator::AnnotationTranslator(void)\" -> AnnoTransResult");
        return (TranslationResult) null;
    }
    
    public TranslationTable getTranslationTableDetails(String tableId) {
        System.err.println("*** WARNING *** not implemented yet: \"AnnotationTranslator::getTranslationTableDetails(String tableId)\" -> TranslationTable");
        return (TranslationTable) null;
    }
}
