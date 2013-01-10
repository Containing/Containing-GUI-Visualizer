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
public class ObjectManager {
    Spatial largeShipTPL, agvTPL, truckTPL;
    Node rootNode, containerNode;

    
    ArrayList<VisualVehicle> boatList;
    ArrayList<VisualVehicle> agvList;
    ArrayList<VisualVehicle> truckList;
    
    
    public ObjectManager(Node rootNode, Node containerNode, AssetManager assetManager){
        this.rootNode = rootNode;
        this.containerNode = containerNode;
        
        loadModels(assetManager);
        
        boatList = new ArrayList<VisualVehicle>();
        
        Spatial model = largeShipTPL.clone();
        model.setLocalTranslation(1000, 0, 00);
        this.rootNode.attachChild(model);
        
    }

    private void loadModels(AssetManager assetManager){
        Container.model = assetManager.loadModel("Models/Container/Container.obj"); 
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_default.setColor("Color", ColorRGBA.White);
        mat_default.setTexture("ColorMap", assetManager.loadTexture("Models/Container/Textures/EpicTransport.png"));
        Container.model.setMaterial(mat_default);
        
        largeShipTPL = assetManager.loadModel("Models/ShipLarge/ShipLarge.obj"); 
        Material mat_ship = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_ship.setTexture("DiffuseMap", assetManager.loadTexture("Models/ShipLarge/Textures/enterprise.png"));
        largeShipTPL.setMaterial(mat_ship);
        largeShipTPL.scale(1.01f);   
        
        agvTPL = assetManager.loadModel("Models/AGV/AGV.obj"); 
        Material mat_agv = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_agv.setTexture("DiffuseMap", assetManager.loadTexture("Models/AGV/Textures/agv.png"));
        agvTPL.setMaterial(mat_agv);
        agvTPL.scale(1.00f); 
        
        truckTPL = assetManager.loadModel("Models/Truck/Truck.obj"); 
        Material mat_truck = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_truck.setTexture("DiffuseMap", assetManager.loadTexture("Models/Truck/Textures/truck.png"));
        truckTPL.setMaterial(mat_truck);
        truckTPL.scale(1.00f);
    }

    
    
    public VisualVehicle addShip(Vehicles.TransportVehicle base){
       try {
       
           Spatial model = largeShipTPL.clone();
 
           VisualVehicle b = new VisualVehicle(   model, 
                                containerNode, 
                                base.GetArrivalDate(), 
                                base.GetDepartureDate(), 
                                base.GetCompany(), 
                                new Helpers.Vector3f(5,5,5), 
                                new Pathfinding.Node(base.getPosition().x, base.getPosition().z));
            b.storage = base.storage;
            //b.setDestination(base.getDestination());
            b.setDestination(new Pathfinding.Node(1000,1000));
            b.setPostion(base.getPosition());
         
            boatList.add(b);
            rootNode.attachChild(model);
            
            return b;
       } catch (Exception ex) {
           Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
    }

    
    /*
    public VisualVehicle addShip(int id, Vector3f position){
        try {
            Spatial model = largeShipTPL.clone();
            model.setLocalTranslation(position);
            
            Node containerNode = new Node();
            VisualVehicle b = new VisualVehicle(model, containerNode, new Date(), new Date(), "Transportcompany", new Helpers.Vector3f(5,5,5), new Pathfinding.Node(10, 10));
            boatList.add(b);
            rootNode.attachChild(model);

            return b;
        } catch (Exception ex) {
            Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    */
    
    public void update(float gameTime){
        for(VisualVehicle b : boatList)b.update(gameTime);
    }   
}
