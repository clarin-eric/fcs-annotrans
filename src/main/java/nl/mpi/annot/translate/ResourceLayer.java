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
public class ResourceLayer {
    //--- this object defines a  Resource / Layer combination.
    // Not only should this combination be unique (cf ATResourceLayers, which checks this),
    // but they uniquely define a formalism, which must be checked too.
    // ATResourceLayers is used to make sure only unique combinations can occur.
    // ATResourceLayers is the only object that is allowed to call the ATResourceLayer constructor.
    
    private String res;
    private String lay;
    private String form;
    private String name;
    
    public ResourceLayer(String r, String l, String f) {
        res = r;
        lay = l;
        form = f;
        name = res + "/" + lay;
    }
    
    String getName() {
        return name;    
    }
    
    String getResource() {
        return res;
    }
    
    String getLayer() {
        return lay;    
    }
    
    String getFormalism() {
        return form;
    }
    
    void display() {
        System.out.println("    ResourceLayer " + name + " => formalism \"" + form + "\"");
    }
}
