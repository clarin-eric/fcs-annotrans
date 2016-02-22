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
 */
public class TranslationResult {
    private String              inText;    // text to be translated
    private String              inFormat;  // description of input format used
    private ArrayList<String>   outTexts;   // result[s] of translation
    private String              outFormat; // description of output format used
    private ArrayList<String>   messages;   // messages generated during the translation process

    public TranslationResult(String i, String iF, String[] o, String oF, String[] m) {
        inText = i;
        inFormat = iF;
        outTexts = new ArrayList<String>();
        for (String s: o) {
            outTexts.add(s);
        }
        outFormat = oF;
        for (String s: m) {
            messages.add(s);
        }
    }
    
    public TranslationResult(String i, String iF) {
        inText = i;
        inFormat = iF;
        outTexts = new ArrayList<String>();
        outFormat = null;
        messages = new ArrayList<String>();
    }
    
    public void setOutTexts(ArrayList<String> texts) {
        outTexts = texts;
    }
    
    public void addOutText(String txt) {
        outTexts.add(txt);
    }

    public void setOutFormat(String oF) {
        outFormat = oF;
    }
    
    public void addMessage(String msg) {
        messages.add(msg);
    }
    
    String getInText() {
        return inText;
    }
    
    String getInFormat() {
        return inFormat;
    }
    
    ArrayList<String> getOutText() {
        return outTexts;
    }
    
    String getOutFormat() {
        return outFormat;
    }
    
    ArrayList<String> getMessages() {
        return messages;
    }
    
    void display() {
        System.out.println("translate \"" + inText + "\" (" + inFormat + " => " + outFormat + ", " + outTexts.size() + " results, " + messages.size() + " messages)");
        for (String s: outTexts) {
            System.out.println("          \"" + s + "\"");
        }
        for (String m: messages) {
            System.out.println("  message " + m);
        }
    }
}
    
