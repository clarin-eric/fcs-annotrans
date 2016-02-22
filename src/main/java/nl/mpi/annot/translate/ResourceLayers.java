/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author petbei
 */
public class ResourceLayers {
    private ArrayList<ResourceLayer> resourceLayerArray = null;
    private HashMap<String, Integer> resourceLayerMap = null;
    
    public ResourceLayers() {
        // System.err.println("--- LOG --- created \"ResourceLayers\" object");
        resourceLayerArray = new ArrayList<ResourceLayer>();
        resourceLayerMap = new HashMap<String, Integer>();
    }
    
    ResourceLayer addResourceLayer(String res, String lay, String form) throws ATException {
        //--- check validity of parameters
        if (res == (String) null || res.length() < 1) {
            System.out.println("*** ERROR *** ResourceLayers: attempt to add NULL or empty Resource name - skipped");
            throw new ATException("definition of Resource/Layer: null resource");
        }
        if (lay == (String) null || lay.length() < 1) {
            System.out.println("*** ERROR *** ResourceLayers: attempt to add NULL or empty Layer name - skipped");
            throw new ATException("definition of Resource/Layer: null layer");
        }
        if (form == (String) null || form.length() < 1) {
            System.out.println("*** ERROR *** ResourceLayers: attempt to add NULL or empty Formalism object - skipped");
            throw new ATException("definition of Resource/Layer: null formalism");
        }
        
        //--- check if Resource/Layer combination is already defined
        String resLayKey = res + "/" + lay;
        if (!resourceLayerMap.containsKey(resLayKey)) {
            //--- not defined; create new one
            ResourceLayer nw = new ResourceLayer(res, lay, form);
            resourceLayerArray.add(nw);
            resourceLayerMap.put(resLayKey, resourceLayerArray.size() - 1);
            return nw;
        }
        else {
            //--- defined. Check if Formalism is the same (should be).
            ResourceLayer rl = resourceLayerArray.get(resourceLayerMap.get(resLayKey));
            String foundForm = rl.getFormalism();
            if (foundForm.equals(form)) {
                //--- Formalism is OK. Can return existing ResourceLayer object.
                return rl;
            }
            else {
                //--- conflicting formalisms. Generate error/exception.
                System.err.println("*** ERROR *** ResourceLayers: attempt to redefine Resource/Layer object \""
                            + res + "/" + lay + "\" with conflicting formalism (" + foundForm + " - " + form + ")- skipped");
                throw new ATException("double definition of Resource/Layer" + resLayKey);
            }
        }
    }
    
    ResourceLayer getResourceLayer(String resLayName) throws ATException {
        if (resLayName == (String) null || resLayName.length() < 1) {
            System.out.println("*** ERROR *** ResourceLayers: attempt to retrieve NULL or empty Resource name - skipped");
            throw new ATException("definition of Resource/Layer: null resource");
        }
        if (resourceLayerMap.containsKey(resLayName)) {
            return resourceLayerArray.get(resourceLayerMap.get(resLayName));
        }
        //--- not defined; error
        System.out.println("*** ERROR *** ResourceLayers: attempt to retrieve undefined resource/layer combination " + resLayName);
        throw new ATException("undefined Resource/Layer: " + resLayName);
    }
    
    void display() {
        System.out.println("========== START list of Resource/Layer combinations ==========");
        for (ResourceLayer rl: resourceLayerArray) {
            rl.display();
        }
        System.out.println("========== END list of Resource/Layer combinations ==========\n");
    }
}
