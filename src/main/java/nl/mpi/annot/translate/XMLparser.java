/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;
import java.io.*;

/**
 *
 * @author petbei
 */
public class XMLparser extends DefaultHandler {
    boolean inElement = false;
    int indentation = 0;

    private static class MyErrorHandler implements ErrorHandler {
        private PrintStream out;

        MyErrorHandler(PrintStream out) {
            this.out = out;
        }

        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }

            String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
            return info;
        }

        public void warning(SAXParseException spe) throws SAXException {
            out.println("Warning: " + getParseExceptionInfo(spe));
        }
        
        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }

    private SymbolTable       resources;
    private SymbolTable       layers;
    private ResourceLayers    resourcesLayers;
    private SymbolTable       formalisms;
    private TranslationTypes  translationTypes;
    private TranslationTables translationTables;
    
    XMLparser(String fileName, SymbolTable atrs, SymbolTable atls, SymbolTable atfs, TranslationTypes tts, ResourceLayers atRLs, TranslationTables atTTs) {
        resources = atrs;
        layers = atls;
        resourcesLayers = atRLs;
        formalisms = atfs;
        translationTypes = tts;
        translationTables = atTTs;
        
        System.out.println("XMLparser input file name: " + fileName);
        if (fileName == null) {
            usage();
        } 
        else {
            try {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                SAXParser saxParser = spf.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();
                //--- xmlReader.setContentHandler(new SAXLocalNameCount());
                xmlReader.setContentHandler(this);
                xmlReader.setErrorHandler(new MyErrorHandler(System.err));
                xmlReader.parse(convertToFileURL(fileName));
            }
            catch (Exception e) {
                System.err.println("*** ERROR *** exception in AnnotationTranslator SAX parser: " + e.getMessage());
            }
        }
        //resources.display();
        //layers.display();
        //resourcesLayers.display();
        //formalisms.display();
        //translationTypes.display();
        //translationTables.display();
    }

    private Hashtable<String,Integer> tags;

    private static void usage() {
        System.err.println("Usage: SAXLocalNameCount <file.xml>");
        System.err.println("       -usage or -help = this message");
        System.exit(1);
    }

    public void startDocument() throws SAXException {
        tags = new Hashtable<String,Integer>();
    }

    private boolean amInAnnotationTranslation = false;  //--- part of state machine that is used in parsing the XML file
    private boolean amInResources = false;              //--- part of state machine that is used in parsing the XML file
    private boolean amInTranslations = false;           //--- part of state machine that is used in parsing the XML file
    private boolean amInTranslationTable = false;       //--- part of state machine that is used in parsing the XML file

    //--- keep track of current TranslationTable, so that we know what to add translation pairs to
    private TranslationTable  curTable = null;
    
    //--- code to track file name / pare line / parse position (for error messages)
    private Locator locator;
    
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }
    
    String getLocator() {
        return ("[" + locator.getSystemId() + " " + locator.getLineNumber() + ":" + locator.getColumnNumber() + "]");
    }
    
    //--- code to create a unique ResourceLayer object; Resource + Layer define Formalism
    void tryAddResourceLayer(Attributes atts) {
        //--- get parameters
        if (atts.getLength() == 3) {
            String resourceValue = null;
            String layerValue = null;
            String formalismValue = null;
            for (int ix = 0; ix < atts.getLength(); ix++) {
                if (atts.getLocalName(ix).equals("resource")) {
                    resourceValue = atts.getValue(ix);
                }
                else if (atts.getLocalName(ix).equals("layer")) {
                    layerValue = atts.getValue(ix);
                }
                else if (atts.getLocalName(ix).equals("formalism")) {
                    formalismValue = atts.getValue(ix);
                }
                else {
                    System.err.println("*** ERROR *** " + getLocator() + " unexpected attribute in \"ResourceLayer\": " + atts.getLocalName(ix) + " - ResourceLayer ignored");
                    return;
                }
            }
            if (resourceValue == null) {
                System.err.println("*** ERROR *** " + getLocator() + " missing \"resource\" attribute in \"ResourceLayer\": ");
                return;
            }
            if (layerValue == null) {
                System.err.println("*** ERROR *** " + getLocator() + " missing \"layer\" attribute in \"ResourceLayer\": ");
                return;
            }
            if (formalismValue == null) {
                System.err.println("*** ERROR *** " + getLocator() + " missing \"formalism\" attribute in \"ResourceLayer\": ");
                return;
            }
            resources.add(resourceValue);
            layers.add(layerValue);
            formalisms.add(formalismValue);
            try {
                resourcesLayers.addResourceLayer(resourceValue, layerValue, formalismValue);
                // System.err.println("INFO " + getLocator() + " added unique ResourceLayer (" + resourceValue + ", " + layerValue + ", " + formalismValue + ")");
            }
            catch (Exception e) {
                System.err.println("*** ERROR *** " + getLocator() + " failed to add unique ResourceLayer (" + resourceValue + ", " + layerValue + ", " + formalismValue + "): " + e.getMessage());
            }
        }
        else {
            System.err.println("*** ERROR *** " + getLocator() + " \"ResourceLayer\" has wrong number of attributes: 3 expected, " + atts.getLength() + " found - ResourceLayer ignored");
        }
    }

    //--- code to create a unique TranslationTable object (from/to ResourceLayer object, translation type object
    void tryAddTranslationTable(Attributes atts) {
        //--- get parameters
        if (atts.getLength() == 3) {
            String fromRLValue = null;
            String toRLValue = null;
            String translationTypeValue = null;
            for (int ix = 0; ix < atts.getLength(); ix++) {
                if (atts.getLocalName(ix).equals("fromResourceLayer")) {
                    fromRLValue = atts.getValue(ix);
                }
                else if (atts.getLocalName(ix).equals("toResourceLayer")) {
                    toRLValue = atts.getValue(ix);
                }
                else if (atts.getLocalName(ix).equals("translationType")) {
                    translationTypeValue = atts.getValue(ix);
                }
                else {
                    System.err.println("*** ERROR *** " + getLocator() + " unexpected attribute in \"TranslationTable\": " + atts.getLocalName(ix) + " - TranslationTable ignored");
                    return;
                }
            }
            if (fromRLValue == null) {
                System.err.println("*** ERROR *** " + getLocator() + " missing \"fromResourceLayer\" attribute in \"TranslationTable\": ");
                return;
            }
            if (toRLValue == null) {
                System.err.println("*** ERROR *** " + getLocator() + " missing \"toResourceLayer\" attribute in \"TranslationTable\": ");
                return;
            }
            if (translationTypeValue == null) {
                System.err.println("*** ERROR *** " + getLocator() + " missing \"translationType\" attribute in \"TranslationTable\": ");
                return;
            }
            try {
                ResourceLayer fromRL = resourcesLayers.getResourceLayer(fromRLValue);
                ResourceLayer toRL = resourcesLayers.getResourceLayer(toRLValue);
                TranslationType tt = translationTypes.addTranslationType(translationTypeValue);
                curTable = translationTables.addTranslationTable(fromRL, toRL, tt);
                //System.err.println("INFO " + getLocator() + " added TranslationTable (" + fromRLValue + ", " + toRLValue + ", " + translationTypeValue);
            }
            catch (Exception e) {
                System.err.println("*** ERROR *** " + getLocator() + " failed to add TranslationTable (" + fromRLValue + ", " + toRLValue + ", " + translationTypeValue + "): " + e.getMessage());
            }
        }
        else {
            System.err.println("*** ERROR *** " + getLocator() + " \"ResourceLayer\" has wrong number of attributes: 3 expected, " + atts.getLength() + " found - ResourceLayer ignored");
        }
    }

    //--- code to add a from / to translation pair to a TranslationTable 
    void tryAddTranslation(Attributes atts) {
        //--- get parameters
        if (curTable == null) {
            System.err.println("*** ERROR *** " + getLocator() + " while processing \"Pair\": no valid TranslationTable available, Pair ignored");
            return;
        }
        if (atts.getLength() != 2) {
            System.err.println("*** ERROR *** " + getLocator() + " \"Pair\" has wrong number of attributes: 2 expected, " + atts.getLength() + " found - Pair ignored");
        }
        String fromValue = null;
        String toValue = null;
        for (int ix = 0; ix < atts.getLength(); ix++) {
            if (atts.getLocalName(ix).equals("from")) {
                fromValue = atts.getValue(ix);
            }
            else if (atts.getLocalName(ix).equals("to")) {
                toValue = atts.getValue(ix);
            }
            else {
                System.err.println("*** ERROR *** " + getLocator() + " unexpected attribute in \"Pair\": " + atts.getLocalName(ix) + " - Pair ignored");
                return;
            }
        }
        if (fromValue == null) {
            System.err.println("*** ERROR *** " + getLocator() + " missing \"from\" attribute in \"Pair\": ");
            return;
        }
        if (toValue == null) {
            System.err.println("*** ERROR *** " + getLocator() + " missing \"to\" attribute in \"Pair\": ");
            return;
        }
        try {
            curTable.addTranslation(fromValue, toValue);
            // System.out.println("INFO " + getLocator() + " added Translation Pair (" + fromValue + ", " + toValue + ")");
        }
        catch (Exception e) {
            System.err.println("*** ERROR *** " + getLocator() + " failed to add Pair (" + fromValue + ", " + toValue + "): " + e.getMessage());
        }
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName, 
                             Attributes atts) throws SAXException {
        inElement = true;
        String key = localName;
        Integer value = tags.get(key);
        // System.err.println("==> XML parse tag \"" + localName + "\"");
        if (localName.equals("AnnotationTranslation")) {
            if (amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " entered \"AnnotationTranslation\" TWICE, ignored");
            }
            else {
                amInAnnotationTranslation = true;
            }
        }
        else if (localName.equals("Resources")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"Resources\" tag encountered while not in \"AnnotationTranslation\"");
                amInAnnotationTranslation = true;
            }
            if (amInResources){
                System.err.println("*** WARNING *** " + getLocator() + " entered \"resources\" TWICE, ignored");
            }
            else {
                amInResources = true;
            }
        }
        else if (localName.equals("ResourceLayer")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"ResourceLayer\" tag encountered while not in \"AnnotationTranslation\"");
                amInAnnotationTranslation = true;
            }
            if (!amInResources){
                System.err.println("*** WARNING *** " + getLocator() + " \"ResourceLayer\" tag encountered while not in \"Resources\"");
                amInResources = true;
            }
            tryAddResourceLayer(atts);
        }
        else if (localName.equals("Translations")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"Translations\" tag encountered while not in \"AnnotationTranslation\"");
                amInAnnotationTranslation = true;
            }
            if (amInTranslations){
                System.err.println("*** WARNING *** " + getLocator() + " entered \"Translations\" TWICE, ignored");
            }
            else {
                amInTranslations = true;
            }
        }
        else if (localName.equals("TranslationTable")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"TranslationTable\" tag encountered while not in \"AnnotationTranslation\"");
                amInAnnotationTranslation = true;
            }
            if (!amInTranslations){
                System.err.println("*** WARNING *** " + getLocator() + " \"TranslationTable\" tag encountered while not in \"Translations\"");
                amInTranslations = true;
            }
            if (amInTranslationTable){
                System.err.println("*** WARNING *** " + getLocator() + " entered \"TranslationTable\" TWICE, ignored");
            }
            else {
                amInTranslationTable = true;
            }
            tryAddTranslationTable(atts);
        }
        else if (localName.equals("Pair")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"Pair\" tag encountered while not in \"AnnotationTranslation\"");
                amInAnnotationTranslation = true;
            }
            if (!amInTranslations){
                System.err.println("*** WARNING *** " + getLocator() + " \"Pair\" tag encountered while not in \"Translations\"");
                amInTranslations = true;
            }
            if (!amInTranslationTable){
                System.err.println("*** WARNING *** " + getLocator() + " \"Pair\" tag encountered while not in \"TranslationTable\"");
                amInTranslationTable = true;
            }
            tryAddTranslation(atts);
        }
        else {
            System.err.println("*** ERROR *** " + getLocator() + " unknown tag \"" + localName + "\", ignored");            
        }
        //--- commented out: code that displays detailed information on current tag attributes
        //indent(indentation);
        //System.out.print("" + namespaceURI + "\"" + localName + "\": " + atts.getLength() + " attributes");
        //for (int i = 0; i < atts.getLength(); i++) {
        //    String attsName = atts.getLocalName(i);
        //    String attsValue = atts.getValue(i);
        //    System.out.print("   [" + i + "] " + attsName + " = \"" + attsValue + "\"");
        //}
        //System.out.println("");
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        //if (inElement) {
        //    System.out.println("characters: =>" + chars/*.toString()*/ + "<= " + i + "," + i1 + " ==>" + new String(chars, i, i1) + "<==");
        //}
        String data = new String(chars, i, i1);
        // Whitespace makes up default StringTokenizer delimeters
        StringTokenizer tok = new StringTokenizer(data);
        if (tok.hasMoreTokens()) {  
            indent(indentation);
            System.out.print("==>" + tok.nextToken());
            while (tok.hasMoreTokens()) {
                System.out.print(" " + tok.nextToken());
            }
            System.out.println("<==");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("AnnotationTranslation")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " end of \"AnnotationTranslation\" while not in it, ignored");
            }
            else {
                amInAnnotationTranslation = false;
            }
        }
        else if (localName.equals("Resources")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"Resources\" end tag encountered while not in \"AnnotationTranslation\"");
                amInAnnotationTranslation = false;
            }
            if (!amInResources){
                System.err.println("*** WARNING *** " + getLocator() + " end of \"resources\" while not in it, ignored");
            }
            else {
                amInResources = false;
            }
        }
        else if (localName.equals("ResourceLayer")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"ResourceLayer\" end tag encountered while not in \"AnnotationTranslation\"");
            }
            if (!amInResources){
                System.err.println("*** WARNING *** " + getLocator() + " \"ResourceLayer\" end tag encountered while not in \"Resources\"");
            }
        }
        else if (localName.equals("Translations")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"Translations\" end tag encountered while not in \"AnnotationTranslation\"");
            }
            if (!amInTranslations){
                System.err.println("*** WARNING *** " + getLocator() + " leaving \"Translations\" while not in translations, ignored");
            }
            else {
                amInTranslations = false;
            }
        }
        else if (localName.equals("TranslationTable")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"TranslationTable\" end tag encountered while not in \"AnnotationTranslation\"");
                amInAnnotationTranslation = true;
            }
            if (!amInTranslations){
                System.err.println("*** WARNING *** " + getLocator() + " \"TranslationTable\" end tag encountered while not in \"Translations\"");
                amInTranslations = true;
            }
            if (!amInTranslationTable){
                System.err.println("*** WARNING *** " + getLocator() + " leaving \"TranslationTable\" TWICE, ignored");
            }
            else {
                amInTranslationTable = false;
                curTable.tryPopulateTrellis();
                curTable = null;
            }
        }
        else if (localName.equals("Pair")) {
            if (!amInAnnotationTranslation){
                System.err.println("*** WARNING *** " + getLocator() + " \"Pair\" end tag encountered while not in \"AnnotationTranslation\"");
                amInAnnotationTranslation = true;
            }
            if (!amInTranslations){
                System.err.println("*** WARNING *** " + getLocator() + " \"Pair\" end tag encountered while not in \"Translations\"");
                amInTranslations = true;
            }
            if (!amInTranslationTable){
                System.err.println("*** WARNING *** " + getLocator() + " \"Pair\" end tag encountered while not in \"TranslationTable\"");
                amInTranslationTable = true;
            }
        }
        else {
            System.err.println("*** ERROR *** " + getLocator() + " unknown end tag \"" + localName + "\", ignored");            
        }
    }

    private void indent(int indentation) {
        for(int i=0; i<indentation; i++) {
            System.out.print(" ");
        }
    }

    public void endDocument() throws SAXException {
        Enumeration e = tags.keys();
        while (e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            int count = ((Integer)tags.get(tag)).intValue();
            System.out.println("Local Name \"" + tag + "\" occurs " + count + " times");
        }    
    }
 
    private static String convertToFileURL(String fileName) {
        String path = new File(fileName).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }
}
