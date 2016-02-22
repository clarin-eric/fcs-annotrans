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
public class TrellisNode {
    private char content;
    private ArrayList<String> targets;
    private TrellisNode son;
    private TrellisNode next;
    
    TrellisNode(char c) {
        content = c;
        son = null;
        next = null;
        targets = new ArrayList<String>();
    }
    
    TrellisNode addTranslation(String src, String tgt) {
        if (src == null) {
            //--- nothing to add, maintain current status at calling side
            System.err.println("TrellisNode.addTranslation(): parameter src is NULL");
            return this;
        }
        if  (src.length() < 1) {
            //--- nothing to add, maintain current status at calling side
            System.err.println("TrellisNode.addTranslation(): parameter src is EMPTY");
            return this;
        }
        if  (tgt == null) {
            //--- nothing to add, maintain current status at calling side
            System.err.println("TrellisNode.addTranslation(): parameter tgt is NULL");
            return this;
        }
        //--- src is at leat length 1
        char sourceChar = src.charAt(0);

        if (sourceChar < content) {
            //--- insert before current node
            TrellisNode newNode = new TrellisNode(sourceChar);
            newNode.next = this;
            if (src.length() <= 1) {
                //--- at end of source, add target
                newNode.targets.add(tgt);
                return newNode;
            }
            else {
                //--- not at end of source, try add remainder of source as son node
                newNode.son = new TrellisNode(src.charAt(1));
                newNode.son = newNode.son.addTranslation(src.substring(1), tgt);
                return newNode;
            }
        }
        else if (sourceChar == content) {
            //--- am at continuation node
            if (src.length() <= 1) {
                //--- at end of source, add target
                targets.add(tgt);
                return this;
            }
            if (son == null) {
                //--- not at end of source, create new son
                son = new TrellisNode(src.charAt(1));
            }
            //--- try to add remainder of source to existing son node
            son = son.addTranslation(src.substring(1), tgt);                    
            return this;
        }
        else {
            //--- sourceChar > content
            if (next == null) {
                //--- at end of list, append new node
                next = new TrellisNode(sourceChar);
                if (src.length() <= 1) {
                    //--- at end of source
                    next.targets.add(tgt);
                    return this;
                }
                next.son = new TrellisNode(src.charAt(1));
                next.son = next.son.addTranslation(src.substring(1), tgt);
                return this;
            }
            else {
                next = next.addTranslation(src, tgt);
                return this;
            }
        }
    }
    
    int translate(String src, ArrayList<ArrayList<String>> inter) {
        int processed = 0;
        if (content < src.charAt(0)) {
            //--- no match, but maybe in next;
            if (next == null) {
                return 0;
            }
            return next.translate(src, inter);
        }
        else if (content > src.charAt(0)) {
            //--- past all possibilities of match
            return 0;
        }
        if (content == src.charAt(0)) {
            //--- match!
            if (src.length() == 1) {
                //--- match AND is last character
                inter.add(targets);    //--- possibly empty targets
                return 1;
            }
            else if (son == null) {
                inter.add(targets);
                return 1;
            }
            else {
                int nextProcessed = son.translate(src.substring(1), inter);
                if (nextProcessed == 0) {
                    inter.add(targets);
                    return 1;
                }
                else {
                    return nextProcessed + 1;
                }
                //--- match, more characters available
            }
        }
        return processed;
    }
    
    void display(int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print("    ");
        }
        System.out.print("TrellisNode '" + content + "': ");
        for (String tgt: targets) {
            System.out.print("\"" + tgt + "\"");
        }
        System.out.println("");
        if (son != null) {
            son.display(indent + 1);
        }
        if (next != null) {
            next.display(indent);
        }
    }
}
