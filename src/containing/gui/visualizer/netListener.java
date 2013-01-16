package containing.gui.visualizer;

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

    ObjectManager objMgr;

    public netListener(ObjectManager objMgr){
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
            //System.out.println(data[0]);

            
            switch(data[0]){    //Operator identifier
                case 0:         //Create new vehicle
                    readNetwork_createVehicle(data);
                    break;
                case 1:         //Update existing vehicle
                    readNetwork_syncVehicle(data);                   
                    break;
                case 2:         //Remove existing vehicle
                    
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
            TransportVehicle v = new TransportVehicle(new Date(), new Date(), "HenkTransport", type, storage, Pathfinder.findClosestNode( pos ) );
            v.setDestination(Pathfinder.findClosestNode(pos));
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
    }
    
    private void readNetwork_destroyVehicle(byte[] data){

    }
}