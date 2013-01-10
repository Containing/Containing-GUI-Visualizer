/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.gui.visualizer;

import Main.Container;
import Main.Database;
import Pathfinding.Pathfinder;
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
        Pathfinding.Pathfinder.generateGrid();
        
        
        sceneNode = new Node();
        containerNode = new Node();

        rootNode.attachChild(sceneNode);
        rootNode.attachChild(containerNode);

        sceneNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        
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
        generateShips();

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
                    int startHeight = findLowestNeighbour(i,j,boat,height);
                    Container c = boat.storage.peakContainer(i, j);
                    
                    if(startHeight>boat.storage.Count(i, j))startHeight=boat.storage.Count(i, j);
                    
                    for(int k = startHeight ; k <= boat.storage.Count(i, j) ; k++){
                        Vector3f pos = new Vector3f(i*2.5f - width*2.5f/2 + 1.25f, 
                                                    k * 2.5f, 
                                                    j*6f - length*6f/2);
                        //objMgr.addContainer(c.getContainNr(), pos);
                       
                        boat.addContainer(c.getContainNr(), pos);
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
            //Database.dumpDatabase();
            
            List<Vehicles.TransportVehicle> GetSeaBoats = Vehicles.MatchVehicles.GetSeaBoats();
            System.out.println(GetSeaBoats.size());
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
    
    private void createHarbor(Node sceneNode){
        Box box = new Box( Vector3f.ZERO, 500,10,500);
        Geometry harborblock = new Geometry("Box", box);
        Material harbormat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        harbormat.setTexture("DiffuseMap", assetManager.loadTexture("Models/Harbor/Textures/concrete.jpg"));
        harborblock.setMaterial(harbormat);
        harborblock.setLocalTranslation(-520, -10, 0);
        sceneNode.attachChild(harborblock);
        
        
        Spatial agv = assetManager.loadModel("Models/AGV/AGV.obj"); 
        Material mat_agv = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        //mat_agv.setColor("Color", ColorRGBA.White);
        mat_agv.setTexture("DiffuseMap", assetManager.loadTexture("Models/AGV/Textures/agv.png"));
        agv.setMaterial(mat_agv);
        agv.setLocalTranslation(-50, 0, 0);
        sceneNode.attachChild(agv);   
        
        Spatial truck = assetManager.loadModel("Models/Truck/Truck.obj"); 
        Material mat_truck = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        //mat_agv.setColor("Color", ColorRGBA.White);
        mat_truck.setTexture("DiffuseMap", assetManager.loadTexture("Models/Truck/Textures/truck.png"));
        truck.setMaterial(mat_truck);
        truck.setLocalTranslation(-55, 0, 0);
        sceneNode.attachChild(truck);
        
        
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        rootNode.addLight(sun);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
    }
    
    private void createRoads(Node sceneNode){
        float roadWidth = 1.0f;
        
        Material roadmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        roadmat.setColor("Color", ColorRGBA.Blue);
        
        
        for(Pathfinding.Path p : Pathfinding.Pathfinder.Paths){
            
            Mesh mesh = new Mesh();
            mesh.setMode(Mesh.Mode.Lines);
            mesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{  p.getPointA().getPosition().x, 
                                                                        p.getPointA().getPosition().y, 
                                                                        p.getPointA().getPosition().z, 
                                                                        p.getPointB().getPosition().x, 
                                                                        p.getPointB().getPosition().y, 
                                                                        p.getPointB().getPosition().z});
            mesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
            Geometry l = new Geometry("line", mesh);
            l.setMaterial(roadmat);
            rootNode.attachChild(l);
            
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
        
        Quad quad = new Quad(4000,4000);
        quad.scaleTextureCoordinates(new Vector2f(6f,6f));
        
        water = new Geometry("water", quad);
        water.setLocalRotation(  new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X) );
        water.setLocalTranslation(-2000, -6, 2000);
        
        Material mat_water = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_water.setColor("Color", ColorRGBA.Blue);
        
        water.setMaterial(mat_water);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }

    
    @Override
    public void simpleUpdate(float tpf) {
        objMgr.update(tpf);
        water.setLocalTranslation(cam.getLocation().x-2000, water.getLocalTranslation().y, cam.getLocation().z+2000);

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
