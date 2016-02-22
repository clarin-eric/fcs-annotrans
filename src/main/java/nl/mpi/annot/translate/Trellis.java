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
 * A Trellis (or this Trellis anyway) is a data structure to store words, 1 character per layer.
 * At the top layer the 'root' node is the start of a linked list of alphabetically sorted TrellisNodes, each pointing to the next list element via the 'next' pointer:
 *         .---.         .---.
 * root -> | a | -next-> | f | -next-> NULL
 *         '---'         '---'
 * (so, in this example words starting with 'a' and 'f' are defined.)
 * 
 * Each node has a 'son' pointer to a (alphabetically sorted) linked list of TrellisNodes that define the continuation of the word:
 *
 *         .---.         .---.
 * root -> | a | -next-> | f | -next-> NULL
 *         '---'         '---'
 *           |
 *          son
 *           |
 *           V
 *         .---.         .---.
 *         | r | -next-> | s | -next-> NULL
 *         '---'         '---'
 * 
 * so that the word (starts) 'ar', 'as' and 'f' are defined.
 * 
 * This trellis is used to store translation pairs for words or word segments. At the appropriate position in the trellis a list of 'targets' can be defined,
 * that signifies that the "source" word in the trellis that ends here can be seen as a complete unit, and has translations.
 * NB: the specific position in the Trellis can still have 'next siblings and 'son's.
 */

public class Trellis {
    private TrellisNode root;   //--- pointer to the top level leftmost Trellis node. Initially null/undefined.
    public Trellis() {
        // System.out.println("Translation Trellis created");
        root = null;
    }
    
    //--- the trellis is built up using source/target String pairs. Adding nodes is done recursively, 1 source letter at the time.
    public void addTranslationPair(String src, String tgt) {
        //--- assertion: src and tgt are neither null nor empty. XML files are currently used to define them, and the nodes attributes are checked for validity during parsing.
        if (root == null && src.length() > 0) {
            //--- first node of trellis is defined here, using the first letter of the source string.
            root = new TrellisNode(src.charAt(0));
        }
        //--- a full source word is added recursively, starting at the first character of the source
        root = root.addTranslation(src, tgt);
    }
    
    ArrayList<String> translate(String source) {
        ArrayList<ArrayList<String>> inter = new ArrayList<ArrayList<String>>();    // intermediate translation results: for each segment recognised an array of Strings. 
        if (root == null || source.length() <= 0) {
            return new ArrayList<String>();
        }
        //=== perform translation per source segment.
        // the TrellisNode 'translate' method
        //     - tells how large the segment is that it recognised
        //     - adds an array of partal translations to the intermediate results
        // If nothing is recognised, the current character is skipped (not translated), and work continues with the next position
        int offset = 0;
        while (offset < source.length()) {
            int processed = root.translate(source.substring(offset), inter);
            if (processed > 0) {
                offset = offset + processed;
            }
            else {
                System.err.println("Segmented translation of " + source + ": position " + offset + " (" + source.charAt(offset) + ") skipped");
                offset = offset + 1;
            }
        }
        
        //=== now, the partial results must be combined. This is done in an inefficient but simple way.
        ArrayList<String> final1 = new ArrayList<String>();
        ArrayList<String> final2 = new ArrayList<String>();
        
        for (ArrayList<String> al: inter) {
            if (final2.size() == 0) {
                //--- no final results yet, start with copying the initial segment's targts to the work-in-progress final1 list
                for (String s: al) {
                    final1.add(s);
                }
            }
            else {
                //--- we have already partial results. They must be combined with the next segment's targets.
                for (String s1: final2) {
                    for (String s2: al) {
                        final1.add(s1 + s2);
                    }
                }
            }
            //--- move temporary results to input for the next round, reset temporary results
            final2 = final1;
            final1 = new ArrayList<String>();
        }
        
        //=== so now the results (if any) are in final2
        //for (String f: final2) {
        //    System.out.println("result \"" + f + "\"");
        //}
        //=== move to a real array
        //String[] result = new String[final2.size()];
        //for (int i = 0; i < final2.size(); i++) {
        //    result[i] = final2.get(i);
        //}
        return final2;
    }
    
    //--- show contents of trellis
    public void display() {
        System.out.println("===== START Trellis =====");
        if (root == null) {
            System.out.println("    no root."); //--- empty trellis
        }
        else {
            root.display(1);    //--- recursive display of nodes; integer parameter indicates indentation level.
        }
        System.out.println("===== END Trellis =====\n");
    }
}
