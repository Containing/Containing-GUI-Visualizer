/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.gui.visualizer;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.scene.shape.Box;

public class container {
    public int id;
    public static Spatial model;
    
    public container(int id, Spatial model, Vector3f position){
        model.setLocalTranslation(position);
        this.model = model;
        this.id = id;
    }
    
    
}
