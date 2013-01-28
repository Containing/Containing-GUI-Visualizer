package containing.gui.visualizer;

import Helpers.Vector3f;
import Pathfinding.Pathfinder;
import Vehicles.TransportVehicle;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zeromq.ZMQ;

/**
 *
 * @author EightOneGulf
 */
public class netListener {
    ZMQ.Context zmqContext; //Network objects
    ZMQ.Socket subscriber;  //

    objectManager objMgr;

    public netListener(objectManager objMgr){
        zmqContext = ZMQ.context(1);
        subscriber = zmqContext.socket(ZMQ.SUB);
        this.objMgr = objMgr;
    }
    
    public void connect(){
        subscriber.connect("tcp://127.0.0.1:6001");  
        subscriber.subscribe("".getBytes());
    }
    
    public void disconnect(){ 
        subscriber.disconnect("");
    }
    
    public void readNetwork(){
        byte[] data;

        while((data=subscriber.recv(ZMQ.NOBLOCK)) != null) {
            System.out.println(data[0]);

            switch(data[0]){    //Operator identifier
                case 0:         //Create new vehicle
                    readNetwork_createVehicle(data);
                    break;
                case 1:         //Update existing vehicle
                    readNetwork_syncVehicle(data);                   
                    break;
                case 2:         //Remove existing vehicle
                    readNetwork_destroyVehicle(data); 
                    break;
                case 3:         //Create storage
                    readNetwork_createStorage(data); 
                    break;
                case 4:         //Remove container from storgae
                    readNetwork_removeContainerFromStorage(data); 
                    break;
                case 5:         //Add container to storgae
                    readNetwork_addContainerOnStorage(data); 
                    break;
                case 6:
                    readNetwork_createCrane(data);
                    break;
                case 7:
                    readNetwork_syncCrane(data);
                    break;
            }
        }
    }

    private void readNetwork_createVehicle(byte[] data){
        System.out.println("Net: creating vehicle");
        int id;
        Helpers.Vector3f pos;
        Helpers.Vector3f dest;
        Helpers.Vector3f storage;
        id = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 1, 4));
        Vehicles.TransportVehicle.VehicleType type = Vehicles.TransportVehicle.VehicleType.AGV;

        pos = new Helpers.Vector3f();
        pos.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 5, 4));
        pos.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 9, 4));
        pos.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 13, 4));

        dest = new Helpers.Vector3f();
        dest.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 17, 4));
        dest.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 21, 4));
        dest.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 25, 4));

        storage = new Helpers.Vector3f();
        storage.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 29, 4));
        storage.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 33, 4));
        storage.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 37, 4));
        storage.x = 5;
        storage.y = 5;
        storage.z = 5;
        
        switch( Helpers.byteHelper.toByte(Helpers.byteHelper.getFromArray(data, 41, 1))){
            case 0: //AGV
                type = Vehicles.TransportVehicle.VehicleType.AGV;
                break;
            case 1: //inlandboat
                type = Vehicles.TransportVehicle.VehicleType.inlandBoat;
                break;
            case 2: //seaboat
                type = Vehicles.TransportVehicle.VehicleType.seaBoat;
                break;
            case 3: //AGV
                type = Vehicles.TransportVehicle.VehicleType.train;
                break;
            case 4: //AGV
                type = Vehicles.TransportVehicle.VehicleType.truck;
                break;
        }

        try {
            //Create new vehicle
            //TransportVehicle v = new TransportVehicle(new Date(), new Date(), "HenkTransport", type, storage, Pathfinder.findClosestNode( pos ) );
            TransportVehicle v = new TransportVehicle(new Date(), new Date(), "HenkTransport", type, storage, Pathfinder.findClosestNode( pos ), null );
            v.setDestination( Pathfinder.findClosestNode( dest ) );
            v.Id = id;
            objMgr.addShip(v);
        } catch (Exception ex) {
            Logger.getLogger(ContainingGUIVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }

    private void readNetwork_syncVehicle(byte[] data){
        int id;
        Helpers.Vector3f pos;
        Helpers.Vector3f dest;

        id = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 1, 4));

        pos = new Helpers.Vector3f();
        pos.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 5, 4));
        pos.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 9, 4));
        pos.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 13, 4));

        dest = new Helpers.Vector3f();
        dest.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 17, 4));
        dest.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 21, 4));
        dest.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 25, 4));   
        
        objMgr.syncVehicle(id, Pathfinder.findClosestNode( pos ), Pathfinder.findClosestNode( dest ));
    }
    
    private void readNetwork_destroyVehicle(byte[] data){
        objMgr.destroyVehicle(Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 1, 4)));
    }
    
    private void readNetwork_createStorage(byte[] data){
        int VehicleId = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 1, 4));
        int containerCount = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 5, 9));
        
        for (int i = 0; i < containerCount; i++) {
            int ContainerId = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 9+16*i, 4));
            Helpers.Vector3f position = new Vector3f();
            position.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 13+16*i, 4));
            position.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 17+16*i, 4));
            position.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 21+16*i, 4));
        }
        // sync stuff
    }
    
    private void readNetwork_addContainerOnStorage(byte[] data){
        int VehicleId = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 1, 4));
        int containerId = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 5, 9));
        Helpers.Vector3f position = new Vector3f();
        position.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 9, 4));
        position.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 13, 4));
        position.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 17, 4));

        // sync stuff
    }
    
    private void readNetwork_removeContainerFromStorage(byte[] data){
        int VehicleId = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 1, 4));
        int containerId = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 5, 9));
        // sync stuff
    }
    
    private void readNetwork_createCrane(byte[] data){
        int VehicleId = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 1, 4));
        Helpers.Vector3f position = new Vector3f();
        position.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 5, 4));
        position.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 9, 4));
        position.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 13, 4));
        float rotation = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 17, 4));
        int index = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 21, 1));
        Crane.Crane.CraneType craneType;
        switch(index)
        {
            case 0:
                craneType = Crane.Crane.CraneType.storage;
                break;
            case 1:
                craneType = Crane.Crane.CraneType.barge;
                break;
            case 2: 
                craneType = Crane.Crane.CraneType.seaship;
                break;
            case 3: 
                craneType = Crane.Crane.CraneType.train;
                break;
            case 4: 
                craneType = Crane.Crane.CraneType.truck;
                break;
        }
        // sync stuff
    }
    
    private void readNetwork_syncCrane(byte[] data){
        int VehicleId = Helpers.byteHelper.toInt(Helpers.byteHelper.getFromArray(data, 1, 4));
        Helpers.Vector3f position = new Vector3f();
        position.x = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 5, 4));
        position.y = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 9, 4));
        position.z = Helpers.byteHelper.toFloat(Helpers.byteHelper.getFromArray(data, 13, 4));
    }
}
