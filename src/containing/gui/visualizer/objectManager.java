/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.gui.visualizer;


import Crane.Crane;
import Crane.StorageCrane;
import Main.Container;
import Pathfinding.Pathfinder;
import Vehicles.AGV;
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
public class objectManager {
    Spatial largeShipTPL, agvTPL, truckTPL, locomotiveTPL, traincartTPL, defaultErrorTPL,
            largeCraneTPL;
    Node rootNode, containerNode;

    
    ArrayList<visualVehicle> boatList;
    ArrayList<visualCrane> craneList;
    
    
    /**
     * 
     * @param rootNode node used for all vehicles
     * @param containerNode node used for all containers
     * @param assetManager assetmanager used to load models
     */
    public objectManager(Node rootNode, Node containerNode, AssetManager assetManager){
        this.rootNode = rootNode;
        this.containerNode = containerNode;
        
        loadModels(assetManager);

        boatList = new ArrayList<visualVehicle>();
        craneList = new ArrayList<visualCrane>();

 
        try{
            int id = 0;
            //Er zijn in totaal  10 zeeschipkranen, 8 binnenvaartkranen, 4 treinkranen en 20 truckkranen 
            for(int i = 0; i < 10; i++){    
                // Initialize 10 seaShipCranes
                Crane c = new Crane(++id, 1, Crane.CraneType.seaship, Pathfinder.parkinglots[i+1], Pathfinder.parkinglots[46]);
                Spatial sp = largeCraneTPL.clone();
                craneList.add(new visualCrane(c.getID(), sp, c.getPosition(), c.getRotation()));
                rootNode.attachChild(sp);
            }
            for(int i = 0 ; i < 8; i++){     
                // Initialize 8 BargeCranes
                Crane c = new Crane(++id, 1, Crane.CraneType.barge, Pathfinder.parkinglots[i+12], Pathfinder.parkinglots[47+ (i/4)]);
                Spatial sp = largeCraneTPL.clone();
                craneList.add(new visualCrane(c.getID(), sp, c.getPosition(), c.getRotation()));
                rootNode.attachChild(sp);
            }        
            for(int i  =0 ; i < 4; i++){         
                // Initialize 4 trainCranes
                Crane c = new Crane(++id, 1, Crane.CraneType.train, Pathfinder.parkinglots[i+41], Pathfinder.parkinglots[69 + (i/2)]);
                Spatial sp = largeCraneTPL.clone();
                craneList.add(new visualCrane(c.getID(), sp, c.getPosition(), c.getRotation()));
                rootNode.attachChild(sp);
            }        
            for (int i = 0; i < 20; i++){          
                // Initialize 20 truckCranes
                Crane c = new Crane(++id, 1, Crane.CraneType.train, Pathfinder.parkinglots[i+21], Pathfinder.parkinglots[i+49]);
                Spatial sp = largeCraneTPL.clone();
                craneList.add(new visualCrane(c.getID(), sp, c.getPosition(), c.getRotation()));
                rootNode.attachChild(sp);
            }   
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        
        for(int i = 0; i < 100; i++){
            try {
                // Set there positions on the parking nodes of each storage crane
                AGV agv = new AGV(i, Pathfinder.parkinglots[71 +i], null);
                addShip(agv);
            } catch (Exception ex) {
                Logger.getLogger(objectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }      
    }

    private void loadModels(AssetManager assetManager){
        container.model = assetManager.loadModel("Models/Container/Container.obj"); 
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_default.setColor("Color", ColorRGBA.White);
        mat_default.setTexture("ColorMap", assetManager.loadTexture("Models/Container/Textures/EpicTransport.png"));
        container.model.setMaterial(mat_default);
        
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
        
        largeCraneTPL = assetManager.loadModel("Models/CraneLarge/CraneLarge.obj"); 
        Material mat_largecrane = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_largecrane.setTexture("DiffuseMap", assetManager.loadTexture("Models/Locomotive/Textures/Locomotive.png"));
        largeCraneTPL.setMaterial(mat_largecrane);
        largeCraneTPL.scale(1.00f);
        
        
        defaultErrorTPL = assetManager.loadModel("Models/Error/Error.obj"); 
        Material mat_error = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_error.setColor("Color", ColorRGBA.Red);
        defaultErrorTPL.setMaterial(mat_error);
    }

    public void addContainer(int vehicleID, int containerID, Helpers.Vector3f position ){
        visualVehicle v = findVehicle(vehicleID);
        if(v!=null){
            Container c = new Container(containerID);
            v.addContainer(containerID, new com.jme3.math.Vector3f(position.x,position.y,position.z));
        }
    }
    
    public void destroyVehicle(int id){
        visualVehicle v = findVehicle(id);
        System.out.println("destroying " + id);
        if(v!=null){
            v.removeVisual();
            boatList.remove(v);
        }
        
    }
    
    public void syncVehicle(int id, Pathfinding.Node pos, Pathfinding.Node dest){
        visualVehicle v = findVehicle(id);
        
        System.out.println("syncing " + id);
        if(v!=null){
            try {
                System.out.println(pos.getPosition().toString());
                System.out.println(dest.toString());
                v.setPostion(pos);
                v.setDestination(dest);
            } catch (Exception ex) {
                Logger.getLogger(objectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public visualVehicle addShip(Vehicles.AGV base){
       try {
           System.out.println("Adding " + base.Id);
           Vehicle.VehicleType type = Vehicle.VehicleType.AGV;

           Spatial model = agvTPL.clone();

           visualVehicle b = new visualVehicle( model, 
                                                containerNode, 
                                                new Date(), 
                                                new Date(), 
                                                "AGV",
                                                type,
                                                new Helpers.Vector3f(5,5,5), 
                                                new Pathfinding.Node(base.getPosition().x, base.getPosition().z));
            System.out.println(base.storage.Count());
            b.Id = base.Id;
            b.storage = base.storage;
            b.setPostion(base.getPosition());

            
            if(base.getDestination()!=null)
                b.setDestination( base.getDestination() );

            System.out.println("Adding to rootnode");
            System.out.println(model.getLocalTranslation().toString());

            boatList.add(b);
            rootNode.attachChild(model);
            
            
            return b;
       } catch (Exception ex) {
           Logger.getLogger(objectManager.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
    }
    
    
    /**
     * 
     * @param base transportvehicle to be displayed
     * @return
     */
    public visualVehicle addShip(Vehicles.TransportVehicle base){
       try {
           System.out.println("Adding " + base.Id);
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
            b.Id = base.Id;
            b.storage = base.storage;
            b.setPostion(base.getPosition());

            
            if(base.getDestination()!=null)
                b.setDestination( base.getDestination() );

            System.out.println("Adding to rootnode");
            System.out.println(model.getLocalTranslation().toString());

            boatList.add(b);
            rootNode.attachChild(model);
            
            System.out.println(model.toString());
            
            return b;
       } catch (Exception ex) {
           Logger.getLogger(objectManager.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
    }

    private visualVehicle findVehicle(int id){
        for(visualVehicle v : boatList){
            if(v.Id==id){
                System.out.println("Found " + id);
                return v;
            }            
        }
        System.out.println("NOT FOUND " + id);
        return null;
    }


    /**
     * 
     * @param gameTime
     */
    public void update(float gameTime){
        for(visualVehicle b : boatList)b.update(gameTime);
    }
}
