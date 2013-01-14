/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.gui.visualizer;

import Helpers.Vector3f;
import Pathfinding.Node;
import java.util.Date;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.scene.*;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
/**
 *
 * @author EightOneGulf
 */
public class VisualVehicle extends Vehicles.TransportVehicle {
    private Spatial model;
    
    private com.jme3.scene.Node privateContainerNode;    
    ArrayList<Container> containerList;
    
    public VisualVehicle(Spatial model, com.jme3.scene.Node containerNode, Date arrivalDate, Date departureDate, String arrivalCompany, Vehicles.Vehicle.VehicleType vehicleType, Vector3f containerArraySize, Node startPosition)throws Exception{
        super(arrivalDate, departureDate, arrivalCompany, vehicleType, containerArraySize, startPosition);
        this.model = model;
        this.privateContainerNode = new com.jme3.scene.Node();
        containerNode.attachChild(privateContainerNode);
        
        containerList = new ArrayList<Container>();

        
        model.setLocalTranslation(startPosition.getPosition().x, startPosition.getPosition().y, startPosition.getPosition().z);
        privateContainerNode.setLocalTranslation(model.getLocalTranslation());
    }    
    
    
    
    
    public void update(float gameTime){
        //Vector3f destination = this.getDestination().getPosition();
        Vector3f destination = new Vector3f(1000,0,1000);
        Vector3f diff = new Vector3f(   destination.x - this.getPosition().x,
                                        destination.y - this.getPosition().y,
                                        destination.z - this.getPosition().z);
        diff.normalize();
        diff.x*=gameTime*5f;
        diff.y*=gameTime*5f;
        diff.z*=gameTime*5f;
        move(diff);
    }
    
    
    
    public void move(Vector3f move){
        model.move(new com.jme3.math.Vector3f(move.x, move.y, move.z));
        model.setLocalRotation(new com.jme3.math.Quaternion().fromAngles(0f, (float) Math.atan2(-move.x, -move.z), 0f));
        this.setPostion( new Helpers.Vector3f(  model.getLocalTranslation().x,
                                                model.getLocalTranslation().y,
                                                model.getLocalTranslation().z));

        privateContainerNode.move(new com.jme3.math.Vector3f(move.x, move.y, move.z));
        privateContainerNode.setLocalRotation(new com.jme3.math.Quaternion().fromAngles(0f, (float) Math.atan2(-move.x, -move.z), 0f));
    }
    
    

    
    public void addContainer(int id, com.jme3.math.Vector3f position){
        Spatial model = Container.model.clone();
        
        Container container = new Container(id, model, position);
        containerList.add(container);
        System.out.println("add container");
        privateContainerNode.attachChild(container.model);
    }

    public boolean destroyContainer(int id){
        for(int i = 0 ; i < containerList.size() ; i++){
            System.out.println(containerList.get(i).id);
            if(containerList.get(i).id==id){
                privateContainerNode.detachChild(containerList.get(i).model);
                containerList.remove(i);
                return true;
            }            
        }
        return false;
    }
    
    
    
    
    
    
}
