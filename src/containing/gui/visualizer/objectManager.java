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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author EightOneGulf
 */
public class objectManager {
    Spatial largeShipTPL;
    Node rootNode, containerNode;

    
    ArrayList<boat> boatList;

    public objectManager(Node rootNode, Node containerNode, AssetManager assetManager){
        container.model = assetManager.loadModel("Models/Container/Container.obj"); 
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_default.setColor("Color", ColorRGBA.White);
        mat_default.setTexture("ColorMap", assetManager.loadTexture("Models/Container/Textures/EpicTransport.png"));
        container.model.setMaterial(mat_default);
        
        largeShipTPL = assetManager.loadModel("Models/ShipLarge/ShipLarge.obj"); 
        Material mat_ship = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        //mat_ship.setColor("Color", ColorRGBA.White);
        mat_ship.setTexture("DiffuseMap", assetManager.loadTexture("Models/ShipLarge/Textures/enterprise.png"));
        largeShipTPL.setMaterial(mat_ship);
        largeShipTPL.scale(1.01f);
        
        this.rootNode = rootNode;
        this.containerNode = containerNode;
        
        
        boatList = new ArrayList<boat>();
        
        Spatial model = largeShipTPL.clone();
        model.setLocalTranslation(1000, 0, 00);
        this.rootNode.attachChild(model);
        
    }

    /*
    public void addShip(int id, Vector3f position){
        Spatial model = largeShipTPL.clone();

        container container = new container(id, model, position);
        containerList.add(container);
        rootNode.attachChild(container.model);
    }*/
    public boat addShip(Vehicles.Boat base){
       try {
           Spatial model = largeShipTPL.clone();
 
           boat b = new boat(   model, 
                                containerNode, 
                                base.GetArrivalDate(), 
                                base.GetDepartureDate(), 
                                base.GetCompany(), 
                                new Helpers.Vector3f(5,5,5), 
                                new Pathfinding.Node(base.getPosition().x, base.getPosition().z));
            b.storage = base.storage;
            //b.setDestination(base.getDestination());
            b.setPostion(base.getPosition());
         
            boatList.add(b);
            rootNode.attachChild(model);
            
            
            b.setDestination(new Pathfinding.Node(1000,0,1000));
            
            return b;
           
       } catch (Exception ex) {
           Logger.getLogger(objectManager.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
    }

    public boat addShip(int id, Vector3f position){
        try {
            Spatial model = largeShipTPL.clone();
            model.setLocalTranslation(position);
            
            Node containerNode = new Node();
            boat b = new boat(model, containerNode, new Date(), new Date(), "Transportcompany", new Helpers.Vector3f(5,5,5), new Pathfinding.Node(10, 10));
            boatList.add(b);
            rootNode.attachChild(model);
            
            return b;
        } catch (Exception ex) {
            Logger.getLogger(objectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public void update(float gameTime){
        for(boat b : boatList)b.update(gameTime);
    }   
}
