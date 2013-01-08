/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.gui.visualizer;

import Main.Container;
import Main.Database;
import Vehicles.Boat;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import com.jme3.scene.*;
import com.jme3.scene.shape.Box;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContainingGUIVisualizer extends SimpleApplication {
    public static void main(String[] args) {
        new ContainingGUIVisualizer().start();
    }
    objectManager objMgr;
    Node sceneNode, containerNode;
    AudioNode audio_ambient;
    @Override
    public void simpleInitApp() {
        sceneNode = new Node();
        containerNode = new Node();
        
        rootNode.attachChild(sceneNode);
        rootNode.attachChild(containerNode);
        
        sceneNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        
        cam.setFrustumFar(50000);
        cam.onFrameChange();
        flyCam.setMoveSpeed(50);
        objMgr = new objectManager(sceneNode, containerNode, assetManager);

        //objMgr.addContainer(0, new Vector3f(0f, 0, 0));
        //objMgr.addContainer(1, new Vector3f(2.5f, 0, 0));
        //objMgr.addShip(-1, new Vector3f(7.0f*2.5f, 0, 45));


        inputManager.addMapping("Remove",  new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, new String[]{"Remove"});

        createWater(sceneNode);

        
        audio_ambient = new AudioNode(assetManager, "Sounds/ambientLoop.ogg", false);
        audio_ambient.setLooping(true);  // activate continuous playing
        audio_ambient.setPositional(true);
        audio_ambient.setLocalTranslation(Vector3f.ZERO.clone());
        audio_ambient.setVolume(3);
        rootNode.attachChild(audio_ambient);
        audio_ambient.play(); // play continuously!

        Logger.getLogger("").setLevel(Level.SEVERE);
        generateShips();
    }
   
    
    private int findLowestNeighbour(int i, int j, Boat boat, int maxheight){
        //Low edge
        if(i<=0 || j <=0 || i>=boat.storage.getWidth()-1 || i>=boat.storage.getLength()-1)return 0;
        
        
        return maxheight;
        
    }
    private void generateContainers(Boat boat){
        int width = boat.storage.getWidth();
        int length = boat.storage.getLength();
        int height = boat.storage.getHeight();
        
        for(int i = 0 ; i < width ; i++){
            for(int j = 0 ; j < length ; j++){
                try {
                    int startHeight = findLowestNeighbour(i,j,boat,height);
                    Container c = boat.storage.peakContainer(i, j);
                    
                    if(startHeight>boat.storage.Count(i, j))startHeight=boat.storage.Count(i, j);
                    
                    for(int k = startHeight ; k <= boat.storage.Count(i, j) ; k++){
                        Vector3f pos = new Vector3f(boat.getPosition().x + i*2.5f - width*2.5f/2 + 1.25f, 
                                                    boat.getPosition().y + k * 2.5f, 
                                                    boat.getPosition().z + j*6f - length*6f/2);
                        objMgr.addContainer(c.getContainNr(), pos);
                    }
                } catch (Exception ex) {
                    
                }
            }            
        }
    }
    
    private void generateShips(){
        int i = 0;
        try {
            //Database.restoreDump();
            XML.XMLBinder.GenerateContainerDatabase("C:/Users/EightOneGulf/Dropbox/containing/XML files/xml7.xml");
            List<Boat> GetSeaBoats = Vehicles.GenerateVehicles.GetSeaBoats();
            System.out.println(GetSeaBoats.size());
            for( Boat b : GetSeaBoats ){
                b.setPostion( new Helpers.Vector3f(b.getPosition().x + i, b.getPosition().y, b.getPosition().z) );
                objMgr.addShip(-1, new Vector3f(b.getPosition().x, b.getPosition().y, b.getPosition().z));
                generateContainers(b);
                i+=75;
                //break;
            }
            
        } catch (Exception ex) {
            Logger.getLogger(ContainingGUIVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /*
        int width = 15;
        int length = 20;
        int height = 5;
        
        for(int i = 0 ; i < width ; i++){
            for(int j = 0 ; j < length ; j++){
                for(int z = 0 ; z < height ; z++){
                    objMgr.addContainer(0, new Vector3f(i*2.5f, z*2.5f, j*6f));
                }
                
            }         
        }*/
    
    }
    
    private void createWater(Node sceneNode){
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(sceneNode);
        
        Vector3f waterLocation = new Vector3f(0,-6,0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_X)));
        
        viewPort.addProcessor(waterProcessor);
    
        waterProcessor.setWaterDepth(40);
        waterProcessor.setDistortionScale(0.15f);
        waterProcessor.setWaveSpeed(0.01f);
        
        Quad quad = new Quad(4000,4000);
        quad.scaleTextureCoordinates(new Vector2f(6f,6f));
        
        Geometry water = new Geometry("water", quad);
        water.setLocalRotation(  new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X) );
        water.setLocalTranslation(-2000, -6, 2000);
        
        Material mat_water = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_water.setColor("Color", ColorRGBA.Blue);
        
        water.setMaterial(mat_water);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }

    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Remove") && !keyPressed) {
                //objMgr.destroyContainer(0);
            }
        }
    };
}
