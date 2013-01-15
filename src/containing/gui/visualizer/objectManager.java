/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.gui.visualizer;


import Vehicles.Vehicle;
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
    Spatial largeShipTPL, agvTPL, truckTPL, locomotiveTPL, traincartTPL, defaultErrorTPL;
    Node rootNode, containerNode;

    
    ArrayList<visualVehicle> boatList;
    
    
    public ObjectManager(Node rootNode, Node containerNode, AssetManager assetManager){
        this.rootNode = rootNode;
        this.containerNode = containerNode;
        
        loadModels(assetManager);

        boatList = new ArrayList<visualVehicle>();
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
        
        locomotiveTPL = assetManager.loadModel("Models/Locomotive/Locomotive.obj"); 
        Material mat_locomotive = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_locomotive.setTexture("DiffuseMap", assetManager.loadTexture("Models/Locomotive/Textures/Locomotive.png"));
        locomotiveTPL.setMaterial(mat_locomotive);
        locomotiveTPL.scale(1.00f);
        
        traincartTPL = assetManager.loadModel("Models/Traincart/Traincart.obj"); 
        Material mat_traincart = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_traincart.setTexture("DiffuseMap", assetManager.loadTexture("Models/Locomotive/Textures/Locomotive.png"));
        traincartTPL.setMaterial(mat_traincart);
        traincartTPL.scale(1.00f);
        
        
        defaultErrorTPL = assetManager.loadModel("Models/Error/Error.obj"); 
        Material mat_error = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_error.setColor("Color", ColorRGBA.Red);
        defaultErrorTPL.setMaterial(mat_error);
    }

    
    
    public visualVehicle addShip(Vehicles.TransportVehicle base){
       try {
           System.out.println("Adding ship");
           Vehicle.VehicleType type = base.GetVehicleType();

           Spatial model = largeShipTPL.clone();

           if(type.equals(Vehicle.VehicleType.seaBoat)) model = this.largeShipTPL.clone();
           else if(type.equals(Vehicle.VehicleType.truck)) model = this.truckTPL.clone();
           else if(type.equals(Vehicle.VehicleType.AGV)) model = this.agvTPL.clone();
           else if(type.equals(Vehicle.VehicleType.train)) model = this.locomotiveTPL.clone();

           visualVehicle b = new visualVehicle( model, 
                                                containerNode, 
                                                base.GetArrivalDate(), 
                                                base.GetDepartureDate(), 
                                                base.GetCompany(),
                                                base.GetVehicleType(),
                                                new Helpers.Vector3f(5,5,5), 
                                                new Pathfinding.Node(base.getPosition().x, base.getPosition().z));
            System.out.println(base.storage.Count());
            b.storage = base.storage;
            b.setPostion(base.getPosition());
         
            
            System.out.println("Adding to rootnode");
            System.out.println(model.getLocalTranslation().toString());
            
            boatList.add(b);
            rootNode.attachChild(model);
            
            System.out.println(model.toString());
            
            return b;
       } catch (Exception ex) {
           Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
    }

    
    /*
    public visualVehicle addShip(int id, Vector3f position){
        try {
            Spatial model = largeShipTPL.clone();
            model.setLocalTranslation(position);
            
            Node containerNode = new Node();
            visualVehicle b = new visualVehicle(model, containerNode, new Date(), new Date(), "Transportcompany", new Helpers.Vector3f(5,5,5), new Pathfinding.Node(10, 10));
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
        for(visualVehicle b : boatList)b.update(gameTime);
    }
}
