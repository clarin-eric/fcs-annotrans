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
public class AnnotationTranslatorTestDriver {
    TranslationType ttWhole = null;
    TranslationType ttSegments = null;
    
    TranslationTable FcsCgnPos = null;
    TranslationTable CgnFcsPos = null;
    TranslationTable FcsCgnPhon = null;
    TranslationTable CgnFcsPhon = null;
    
    final static String ANNOTATION_TRANSLATION_FILENAME = "annotationTest_OK_003.XML";
    
    boolean getTranslationTypeData(AnnotationTranslator at) {
        ttWhole = at.getTranslationType("replaceWhole");
        if (ttWhole == null) {
            System.err.println("*** ERROR *** failed to find annotation TranslationType \"replaceWhole\"");
            return false;
        }
        System.err.println("INFO found translation type \"replaceWhole\"");
        ttSegments = at.getTranslationType("replaceSegments");
        if (ttSegments == null) {
            System.err.println("*** ERROR *** failed to find annotation TranslationType \"replaceSegments\"");
            return false;
        }
        System.err.println("INFO found translation type \"replaceSegments\"");
        return true;
    }
    
    boolean getTranslationTableData(AnnotationTranslator at) {
        //--- locate translation table data
        FcsCgnPos = at.findTranslationTable("FCSaggregator", "PoS", "CGN", "PoS", ttWhole);
        if (FcsCgnPos == null) {
            System.err.println("*** ERROR *** failed to find TranslationTable \"FCSaggregator/PoS - CGN/PoS\"");
            return false;
        }
        System.err.println("INFO found TranslationTable \"FCSaggregator/PoS - CGN/PoS\"");

        CgnFcsPos = at.findTranslationTable("CGN", "PoS", "FCSaggregator", "PoS", ttWhole);
        if (CgnFcsPos == null) {
            System.err.println("*** ERROR *** failed to find TranslationTable \"CGN/PoS - FCSaggregator/PoS\"");
            return false;
        }
        System.err.println("INFO found TranslationTable \"CGN/PoS - FCSaggregator/PoS\"");

        FcsCgnPhon = at.findTranslationTable("FCSaggregator", "phonetic", "CGN", "phonetic", ttSegments);
        if (FcsCgnPhon == null) {
            System.err.println("*** ERROR *** failed to find TranslationTable \"FCSaggregator/Phon - CGN/Phon\"");
            return false;
        }
        System.err.println("INFO found TranslationTable \"FCSaggregator/Phon - CGN/Phon\"");
        
        CgnFcsPhon = at.findTranslationTable("CGN", "phonetic", "FCSaggregator", "phonetic", ttSegments);
        if (CgnFcsPhon == null) {
            System.err.println("*** ERROR *** failed to find TranslationTable \"CGN/Phon - FCSaggregator/Phon\"");
            return false;
        }
        System.err.println("INFO found TranslationTable \"CGN/Phon - FCSaggregator/Phon\"");
        return true;
    }
    
    void testPoSTranslations(AnnotationTranslator at) {
        System.out.println("========== FCS aggregator -> CGN ==========");
        FcsCgnPos.translate("NOUN").display();
        FcsCgnPos.translate("Abu Dabhi").display();
        FcsCgnPos.translate("").display();
        FcsCgnPos.translate("ADJ").display();
        System.out.println("========== CGN -> FCS aggregator ==========");
        CgnFcsPos.translate("N").display();
        CgnFcsPos.translate("ADJ").display();
        System.out.println("========== FCS aggregator -> CGN -> FCS aggregator ==========");
        TranslationResult tr1 = FcsCgnPos.translate("NOUN");
        tr1.display();
        for (String s: tr1.getOutText()) {
            TranslationResult tr2 = CgnFcsPos.translate(s);
            tr2.display();
        }
        System.out.println("---------- FCS aggregator -> CGN -> FCS aggregator (cont.)");
        tr1 = FcsCgnPos.translate("ADV");
        tr1.display();
        for (String s: tr1.getOutText()) {
            TranslationResult tr2 = CgnFcsPos.translate(s);
            tr2.display();
        }
        System.out.println("========== CGN -> FCS aggregator -> CGN ==========");
        TranslationResult tr3 = CgnFcsPos.translate("VNW");
        tr3.display();
        for (String s: tr3.getOutText()) {
            TranslationResult tr4 = FcsCgnPos.translate(s);
            tr4.display();
        }
    }
    
    void testPhonTranslations(AnnotationTranslator at) {
        System.out.println("========== FCS aggregator -> CGN ==========");
        FcsCgnPhon.translate("A:").display();
        FcsCgnPhon.translate("A:bu Dabhi").display();
        FcsCgnPhon.translate("A:A:").display();
        FcsCgnPhon.translate("XA:YA:Z").display();
    }
    
    void testTrellis() {
        Trellis tr = new Trellis();
        tr.addTranslationPair("bb", "BB");    // insert first node
        tr.addTranslationPair("cc", "CC");    // insert before first
        tr.addTranslationPair("ab", "AB1");   // append
        tr.addTranslationPair("aa", "AA1");   // append
        tr.addTranslationPair("ac", "AC1");   // append
        tr.addTranslationPair("ac", "AC2");   // append
        tr.addTranslationPair("d", "Drietand");
        tr.addTranslationPair("d", "Viertand");
        tr.addTranslationPair("a", "Avondrood");
        tr.addTranslationPair("", "leeg");
        tr.addTranslationPair(null, "null");
        tr.addTranslationPair("notarget", null);
        tr.addTranslationPair("waterschade", "een");
        tr.addTranslationPair("waterschadde", "ander");
        tr.display();
        for (String s: tr.translate("ac")) {
            System.out.println("ac" + " -> " + s);
        }
        for (String s: tr.translate("acbb")) {
            System.out.println("acbb" + " -> " + s);
        }
        for (String s: tr.translate("acddwaterschaddebb")) {
            System.out.println("acddwaterschaddebb" + " -> " + s);
        }
        for (String s: tr.translate("acXbb")) {
            System.out.println("acXbb" + " -> " + s);
        }
        for (String s: tr.translate("Xacbb")) {
            System.out.println("Xacbb" + " -> " + s);
        }
        for (String s: tr.translate("acbbX")) {
            System.out.println("acbbX" + " -> " + s);
        }
        for (String s: tr.translate("XacYbbZ")) {
            System.out.println("XacYbbZ" + " -> " + s);
        }
    }
    
    void testTranslator(final String annotationTranslationFileName) {
        //--- get translation information from XML file
        AnnotationTranslator at = new AnnotationTranslator(annotationTranslationFileName);
        if (at == null) {
            System.err.println("*** ERROR *** failed to load AnnotationTranslator from file \"" + annotationTranslationFileName + "\"");
            return;
        }
        if (!getTranslationTypeData(at)) {
            return;
        }
        if (!getTranslationTableData(at)) {
            return;
        }
        testPoSTranslations(at);
        testPhonTranslations(at);        
    }
        
    AnnotationTranslatorTestDriver(final String annotationTranslationFileName) {
        //testTrellis();
        testTranslator(annotationTranslationFileName);
    }
    
    public static void main(String[] arguments) {
        AnnotationTranslatorTestDriver attd;
	if (arguments.length > 0) {
	    attd = new AnnotationTranslatorTestDriver(arguments[0]);
	} else {
	    attd = new AnnotationTranslatorTestDriver(ANNOTATION_TRANSLATION_FILENAME);
	}
    }
};
