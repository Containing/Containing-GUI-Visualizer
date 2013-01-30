/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.gui.visualizer;

import com.jme3.scene.Spatial;

/**
 *
 * @author EightOneGulf
 */
public class visualCrane {
    public visualCrane(int id, Spatial model, Helpers.Vector3f position, float rotation){
        if(position!=null){
            System.out.println(position.toString());
            model.setLocalTranslation(position.x, position.y, position.z);
            model.setLocalRotation(new com.jme3.math.Quaternion().fromAngles(0f, (rotation-90)*0.0174532925f, 0f));
        }
    }
}
