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
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author EightOneGulf
 */
public class visualVehicle extends Vehicles.TransportVehicle {
    private Spatial model;
    
    private com.jme3.scene.Node privateContainerNode;    
    ArrayList<container> containerList;
    
    /**
     * 
     * @param model
     * @param containerNode
     * @param arrivalDate
     * @param departureDate
     * @param arrivalCompany
     * @param vehicleType
     * @param containerArraySize
     * @param startPosition
     * @throws Exception
     */
    public visualVehicle(Spatial model, com.jme3.scene.Node containerNode, Date arrivalDate, Date departureDate, String arrivalCompany, Vehicles.Vehicle.VehicleType vehicleType, Vector3f containerArraySize, Node startPosition)throws Exception{
        super(arrivalDate, departureDate, arrivalCompany, vehicleType, containerArraySize, startPosition, null);
        this.speed *= 10;
        
        this.model = model;
        this.privateContainerNode = new com.jme3.scene.Node();
        containerNode.attachChild(privateContainerNode);
        
        containerList = new ArrayList<container>();

        
        model.setLocalTranslation(startPosition.getPosition().x, startPosition.getPosition().y, startPosition.getPosition().z);
        privateContainerNode.setLocalTranslation(model.getLocalTranslation());
    }    

    public void update(float gameTime){
        try {
            Vector3f diff = new Vector3f(this.position.x,this.position.y,this.position.z);
            super.update(gameTime);
            diff.x-=this.position.x;
            diff.y-=this.position.y;
            diff.z-=this.position.z;
            
            this.model.setLocalTranslation(this.position.x, this.position.y, this.position.z);
            if(diff.x!=0||diff.z!=0){
                diff.normalize();
                model.setLocalRotation(new com.jme3.math.Quaternion().fromAngles(0f, (float) Math.atan2(diff.x, diff.z), 0f));
            }
            /*
            try{
                Vector3f nextNode = this.getDestination().getPosition();

                Vector3f diff = new Vector3f(   nextNode.x - this.getPosition().x,
                                                nextNode.y - this.getPosition().y,
                                                nextNode.z - this.getPosition().z);
                diff.normalize();

                if(!(diff.x==0&&diff.y==0)){
                    diff.x*=gameTime*5f;
                    diff.y*=gameTime*5f;
                    diff.z*=gameTime*5f;
                    move(diff);
                }
            }catch(Exception e){
                
            }*/
        } catch (Exception ex) {
            Logger.getLogger(visualVehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    /**
     * 
     * @param move
     */
    public void move(Vector3f move){

            model.move(new com.jme3.math.Vector3f(move.x, move.y, move.z));
            this.setPostion( new Helpers.Vector3f(  model.getLocalTranslation().x,
                                                    model.getLocalTranslation().y,
                                                    model.getLocalTranslation().z));
            privateContainerNode.move(new com.jme3.math.Vector3f(move.x, move.y, move.z));


            model.setLocalRotation(new com.jme3.math.Quaternion().fromAngles(0f, (float) Math.atan2(-move.x, -move.z), 0f));
            privateContainerNode.setLocalRotation(new com.jme3.math.Quaternion().fromAngles(0f, (float) Math.atan2(-move.x, -move.z), 0f));
        
    }
    
    

    
    /**
     * 
     * @param id
     * @param position
     */
    public void addContainer(int id, com.jme3.math.Vector3f position){
        Spatial model = container.model.clone();
        
        container container = new container(id, model, position);
        containerList.add(container);
        System.out.println("add container");
        privateContainerNode.attachChild(container.model);
    }

    /**
     * 
     * @param id
     * @return
     */
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
