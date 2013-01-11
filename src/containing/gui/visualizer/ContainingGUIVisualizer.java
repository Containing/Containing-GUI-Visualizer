/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.gui.visualizer;

import Main.Container;
import Main.Database;
import Pathfinding.Pathfinder;
import Vehicles.TransportVehicle;
import Vehicles.Vehicle;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContainingGUIVisualizer extends SimpleApplication {
    public static void main(String[] args) {
        new ContainingGUIVisualizer().start();
    }
    
    Geometry water;
    ObjectManager objMgr;
    Node sceneNode, containerNode;
    AudioNode audio_ambient, audio_picard;
    @Override
    public void simpleInitApp() {
        Pathfinding.Pathfinder.generateArea();
        

        sceneNode = new Node();
        containerNode = new Node();

        rootNode.attachChild(sceneNode);
        rootNode.attachChild(containerNode);

        sceneNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        //sceneNode.attachChild(SkyFactory.createSky(assetManager, "Skybox/default.png", false));
        
        cam.setFrustumFar(50000);
        cam.onFrameChange();
        cam.setLocation(new Vector3f(-100,10,10));
        flyCam.setMoveSpeed(50);
        objMgr = new ObjectManager(sceneNode, containerNode, assetManager);

        //objMgr.addContainer(0, new Vector3f(0f, 0, 0));
        //objMgr.addContainer(1, new Vector3f(2.5f, 0, 0));
        //objMgr.addShip(-1, new Vector3f(7.0f*2.5f, 0, 45));

/*
        inputManager.addMapping("Forward",  new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Reverse",  new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Left",  new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(actionListener, new String[]{"Forward"});
        inputManager.addListener(actionListener, new String[]{"Reverse"});
        inputManager.addListener(actionListener, new String[]{"Left"});
        inputManager.addListener(actionListener, new String[]{"Right"});*/

        createWater(sceneNode);
        createHarbor(sceneNode);
        createRoads(sceneNode);
        
        audio_ambient = new AudioNode(assetManager, "Sounds/ambientLoop.ogg", false);
        audio_ambient.setLooping(true);  // activate continuous playing
        audio_ambient.setPositional(true);
        audio_ambient.setLocalTranslation(Vector3f.ZERO.clone());
        audio_ambient.setVolume(3);
        rootNode.attachChild(audio_ambient);
        
        audio_picard = new AudioNode(assetManager, "Sounds/the_picard_song.ogg", false);
        audio_picard.setLooping(true);  // activate continuous playing
        audio_picard.setPositional(true);
        audio_picard.setLocalTranslation(Vector3f.ZERO.clone());
        audio_picard.setVolume(0);
        rootNode.attachChild(audio_picard);

        Logger.getLogger("").setLevel(Level.SEVERE);
        //generateVehicles();

        audio_ambient.play(); // play continuously!
        audio_picard.play(); // play continuously!
    }
   
    
    private int findLowestNeighbour(int i, int j, Vehicles.TransportVehicle boat, int maxheight){
        //Low edge
        
        if(i<=0 || j <=0 || i>=boat.storage.getWidth()-1 || i>=boat.storage.getLength()-1)return 0;
        
        return maxheight;
    }
    
    private void generateContainers(VisualVehicle boat){
        int width = boat.storage.getWidth();
        int length = boat.storage.getLength();
        int height = boat.storage.getHeight();
        
        for(int i = 0 ; i < width ; i++){
            for(int j = 0 ; j < length ; j++){
                try {
                    int startHeight = findLowestNeighbour(i,j,boat,height-1);
                    
                    Container c = boat.storage.peakContainer(i, j);
                    
                    if(startHeight>boat.storage.Count(i, j))startHeight=boat.storage.Count(i, j);
                    
                    for(int k = startHeight ; k < boat.storage.Count(i, j) ; k++){
                        Vector3f pos = new Vector3f(i*2.5f - width*2.5f/2 + 1.25f, 
                                                    k * 2.5f, 
                                                    j*6f - length*6f/2 + 3f);
                        //objMgr.addContainer(c.getContainNr(), pos);
                       
                        boat.addContainer(c.getContainNr(), pos);
                    }
                } catch (Exception ex) {
                    
                }
            }            
        }
    }
    
    
    private void generateVehicles(){
        int i = 0;
        try {
            //Database.restoreDump();
            XML.XMLBinder.GenerateContainerDatabase("C:/Users/EightOneGulf/Dropbox/containing/XML files/xml7.xml");
            //Database.dumpDatabase();

             

            
            List<Vehicles.TransportVehicle> GetSeaBoats = Vehicles.MatchVehicles.GetTrucks();
            for( Vehicles.TransportVehicle b : GetSeaBoats ){
                b.setPostion( new Helpers.Vector3f(b.getPosition().x + i, b.getPosition().y, b.getPosition().z) );
                VisualVehicle bs = objMgr.addShip(b);
                generateContainers(bs);
                i+=75;
                break;
            }
            
            GetSeaBoats = Vehicles.MatchVehicles.GetSeaBoats();
            for( Vehicles.TransportVehicle b : GetSeaBoats ){
                b.setPostion( new Helpers.Vector3f(b.getPosition().x + i, b.getPosition().y, b.getPosition().z) );
                VisualVehicle bs = objMgr.addShip(b);
                generateContainers(bs);
                i+=75;
                break;
            }

            
        } catch (Exception ex) {
            Logger.getLogger(ContainingGUIVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    private void createHarbor(Node sceneNode){
        Box box = new Box( Vector3f.ZERO, 500,10,500);
        Geometry harborblock = new Geometry("Box", box);
        Material harbormat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        harbormat.setTexture("DiffuseMap", assetManager.loadTexture("Models/Harbor/Textures/concrete.jpg"));
        harborblock.setMaterial(harbormat);
        harborblock.setLocalTranslation(-520, -10, 0);
        sceneNode.attachChild(harborblock);
        
                
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        rootNode.addLight(sun);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
    }
    
    private void createRoads(Node sceneNode){
        float roadWidth = 8f;
        
        Material roadmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        roadmat.setColor("Color", ColorRGBA.DarkGray);        
        Material nodemat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        nodemat.setColor("Color", ColorRGBA.Red);
        
        Box box = new Box( Vector3f.ZERO, 0.5f,0.5f,0.5f);
        for(Pathfinding.Path p : Pathfinding.Pathfinder.Paths){
            Geometry road = new Geometry("Box", box);
            road.setMaterial(roadmat);

            road.setLocalTranslation(   (p.getPointA().getPosition().x+p.getPointB().getPosition().x)/2, 
                                        (p.getPointA().getPosition().y+p.getPointB().getPosition().y)/2, 
                                        (p.getPointA().getPosition().z+p.getPointB().getPosition().z)/2);


            Helpers.Vector3f diff = new Helpers.Vector3f(   p.getPointB().getPosition().x - p.getPointA().getPosition().x,
                                                            p.getPointB().getPosition().y - p.getPointA().getPosition().y,
                                                            p.getPointB().getPosition().z - p.getPointA().getPosition().z);
            diff.normalize();
            
            
            road.setLocalScale( roadWidth,
                                0.1f,
                                Helpers.Vector3f.distance(p.getPointA().getPosition(), p.getPointB().getPosition()));
            
            road.setLocalRotation(new Quaternion().fromAngles(0f, (float) Math.atan2(-diff.x, -diff.z), 0f));
            road.setMaterial(roadmat);
            rootNode.attachChild(road);
        }
        for(Pathfinding.Node n : Pathfinding.Pathfinder.Nodes){
            Geometry road = new Geometry("Box", box);
            road.setMaterial(roadmat);

            road.setLocalTranslation(   n.getPosition().x, 
                                        n.getPosition().y+5, 
                                        n.getPosition().z);
            road.setMaterial(nodemat);
            rootNode.attachChild(road);
        }
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
        
        Quad quad = new Quad(10000,10000);
        quad.scaleTextureCoordinates(new Vector2f(6f,6f));
        
        water = new Geometry("water", quad);
        water.setLocalRotation(  new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X) );
        water.setLocalTranslation(-5000, -6, 5000);
        
        Material mat_water = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_water.setColor("Color", ColorRGBA.Blue);
        
        water.setMaterial(mat_water);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }

    
    @Override
    public void simpleUpdate(float tpf) {
        objMgr.update(tpf);
        water.setLocalTranslation(cam.getLocation().x-5000, water.getLocalTranslation().y, cam.getLocation().z+5000);

        //Set picard volume
        if(objMgr.boatList.size()>0){
            float leastDistance = 999999;
            Helpers.Vector3f camPos = new Helpers.Vector3f(cam.getLocation().x, cam.getLocation().y, cam.getLocation().z);
            
            for(VisualVehicle b : objMgr.boatList){
                float curDist = Helpers.Vector3f.distance(camPos, b.getPosition());
                if(curDist<leastDistance)leastDistance = curDist;
            }
            
            float maxVolume = 1f;
            float divider = 100f;
            leastDistance/=divider;
            maxVolume-=leastDistance;
            if(maxVolume<0)maxVolume=0;
            audio_picard.setVolume(maxVolume);
        }else{
            audio_picard.setVolume(0);
        }
    }    
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Forward")) {
                //objMgr.boatList.get(0).move(1f, 0f);
                System.out.println("Forward");
            }            
            if (name.equals("Reverse")) {
                //objMgr.boatList.get(0).move(-1f, 0f);
            }         
            if (name.equals("Left") && !keyPressed) {
                //objMgr.boatList.get(0).move(0f, -1f);
            }         
            if (name.equals("Right") && !keyPressed) {
                //objMgr.boatList.get(0).move(0f, 1f);
            }
        }
    };
}
