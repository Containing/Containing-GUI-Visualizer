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
import java.util.ArrayList;
/**
 *
 * @author EightOneGulf
 */
public class objectManager {
    Spatial containerTPL, largeShipTPL;
    Node rootNode, containerNode;
    
    ArrayList<container> containerList;
    
    public objectManager(Node rootNode, Node containerNode, AssetManager assetManager){
        containerTPL = assetManager.loadModel("Models/Container/Container.obj"); 
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_default.setColor("Color", ColorRGBA.White);
        mat_default.setTexture("ColorMap", assetManager.loadTexture("Models/Container/Textures/EpicTransport.png"));
        containerTPL.setMaterial(mat_default);
        
        largeShipTPL = assetManager.loadModel("Models/ShipLarge/ShipLarge.obj"); 
        Material mat_ship = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_ship.setColor("Color", ColorRGBA.White);
        mat_ship.setTexture("ColorMap", assetManager.loadTexture("Models/ShipLarge/Textures/enterprise.png"));
        largeShipTPL.setMaterial(mat_ship);
        largeShipTPL.scale(1.01f);
        
        this.rootNode = rootNode;
        this.containerNode = containerNode;
        
        containerList = new ArrayList<container>();
        
        
        
    }
    
    
    public void addShip(int id, Vector3f position){
        Spatial model = largeShipTPL.clone();
        
        container container = new container(id, model, position);
        containerList.add(container);
        rootNode.attachChild(container.model);
    }
    
    public void addContainer(int id, Vector3f position){
        Spatial model = containerTPL.clone();
        
        container container = new container(id, model, position);
        containerList.add(container);
        containerNode.attachChild(container.model);
    }
    
    public boolean destroyContainer(int id){
        for(int i = 0 ; i < containerList.size() ; i++){
            System.out.println(containerList.get(i).id);
            if(containerList.get(i).id==id){
                containerNode.detachChild(containerList.get(i).model);
                containerList.remove(i);
                return true;
            }            
        }
        return false;
    }
    
    
}
