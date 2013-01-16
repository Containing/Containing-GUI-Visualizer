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
import com.jme3.font.BitmapText;
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
import org.zeromq.ZMQ;

/**
 * 
 * @author EightOneGulf
 */
public class ContainingGUIVisualizer extends SimpleApplication {
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        new ContainingGUIVisualizer().start();
    }

    Geometry water;
    ObjectManager objMgr;           //manages core objects, such as vehicles
    Node sceneNode, containerNode;  //nodes for spatial models. sceneNode is rendered in waterreflection. containerNode isn't
    AudioNode audio_ambient, audio_boat, audio_truck, audio_train;  
   
    netListener netlistener;
    
    /**
     * Starts the jMonkey application
     */
    @Override
    public void simpleInitApp() {
        try { Pathfinding.Pathfinder.generateArea(); }  //Generate nodes and paths
        catch(Exception ex) {};

        sceneNode = new Node();
        containerNode = new Node();
        rootNode.attachChild(sceneNode);
        rootNode.attachChild(containerNode);

        sceneNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));  //Create skybox

        flyCam.setMoveSpeed(500);
        cam.setFrustumFar(50000);   //Set drawdistance
        cam.setFrustumNear(1f);
        cam.onFrameChange();        //Apply config to camera
        cam.setLocation(new Vector3f(1581.5f,10,22.5f));
        
        
        objMgr = new ObjectManager(sceneNode, containerNode, assetManager); 
        createWater(sceneNode);
        createHarbor(sceneNode);
        createRoads(sceneNode);
        createAudio(rootNode);
        

        netlistener = new netListener(objMgr);
        netlistener.connect();
        
        
        
        try {
            TransportVehicle boat = new TransportVehicle(new Date(), new Date(), "bedrijf", Vehicle.VehicleType.seaBoat, new Helpers.Vector3f(10,10,10), Pathfinder.Nodes[172]);
            boat.setDestination(Pathfinder.Nodes[152]);
            objMgr.addShip(boat);
            
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

    
    private int findLowestNeighbour(int i, int j, Vehicles.TransportVehicle boat, int maxheight){
        //Low edge
        
        if(i<=0 || j <=0 || i>=boat.storage.getWidth()-1 || i>=boat.storage.getLength()-1)return 0;
        
        return maxheight;
    }
    
    private void generateContainers(visualVehicle boat){
        int width = boat.storage.getWidth();
        int length = boat.storage.getLength();
        int height = boat.storage.getHeight();
        
        for(int i = 0 ; i < width ; i++){
            for(int j = 0 ; j < length ; j++){
                try {
                    int startHeight = findLowestNeighbour(i,j,boat,height-1);
                    
                    //Container c = boat.storage.peakContainer(i, j);
                    
                    if(startHeight>boat.storage.Count(i, j))startHeight=boat.storage.Count(i, j);
                    
                    for(int k = startHeight ; k < boat.storage.Count(i, j) ; k++){
                        Vector3f pos = new Vector3f(i*2.5f - width*2.5f/2 + 1.25f, 
                                                    k * 2.5f, 
                                                    j*6f - length*6f/2 + 3f);
                        //objMgr.addContainer(c.getContainNr(), pos);
                       
                        //boat.addContainer(c.getContainNr(), pos);
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
                visualVehicle bs = objMgr.addShip(b);
                generateContainers(bs);
                i+=150;
                break;
            }
            
            GetSeaBoats = Vehicles.MatchVehicles.GetSeaBoats();
            for( Vehicles.TransportVehicle b : GetSeaBoats ){
                b.setPostion( new Helpers.Vector3f(b.getPosition().x + i, b.getPosition().y, b.getPosition().z) );
                visualVehicle bs = objMgr.addShip(b);
                generateContainers(bs);
                i+=150;
                break;
            }
            
            GetSeaBoats = Vehicles.MatchVehicles.GetTrains();
            for( Vehicles.TransportVehicle b : GetSeaBoats ){
                b.setPostion( new Helpers.Vector3f(b.getPosition().x + i, b.getPosition().y, b.getPosition().z) );
                visualVehicle bs = objMgr.addShip(b);
                generateContainers(bs);
                i+=150;
                break;
            }

            
        } catch (Exception ex) {
            Logger.getLogger(ContainingGUIVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    private void createAudio(Node node){
        audio_ambient = new AudioNode(assetManager, "Sounds/ambientLoop.ogg", false);
        audio_ambient.setLooping(true);  // activate continuous playing
        audio_ambient.setPositional(true);
        audio_ambient.setLocalTranslation(Vector3f.ZERO.clone());
        audio_ambient.setVolume(1);
        node.attachChild(audio_ambient);
        
        audio_boat = new AudioNode(assetManager, "Sounds/boat_ambient.ogg", false);
        audio_boat.setLooping(true);  // activate continuous playing
        audio_boat.setPositional(true);
        audio_boat.setLocalTranslation(Vector3f.ZERO.clone());
        audio_boat.setVolume(0);
        node.attachChild(audio_boat);     
        
        audio_truck = new AudioNode(assetManager, "Sounds/truck_ambient.ogg", false);
        audio_truck.setLooping(true);  // activate continuous playing
        audio_truck.setPositional(true);
        audio_truck.setLocalTranslation(Vector3f.ZERO.clone());
        audio_truck.setVolume(0);
        node.attachChild(audio_truck);     
        
        audio_train = new AudioNode(assetManager, "Sounds/train_ambient.ogg", false);
        audio_train.setLooping(true);  // activate continuous playing
        audio_train.setPositional(true);
        audio_train.setLocalTranslation(Vector3f.ZERO.clone());
        audio_train.setVolume(0);
        node.attachChild(audio_train);
        
        
        audio_ambient.play(); // play continuously!
        audio_boat.play(); // play continuously!
        audio_truck.play(); // play continuously!
        audio_train.play(); // play continuously!
    }

    private void createHarbor(Node sceneNode){
        float centerX = (Pathfinder.pathWidth*2*(Pathfinder.gapBetweenRoads+5)+Pathfinder.storageLenght)/2;
        float centerZ = (Pathfinder.pathWidth*2*(Pathfinder.gapBetweenRoads+5)+Pathfinder.storageWidth)/2;
        
        Box box = new Box( new Vector3f(centerX, 0, centerZ), centerX,10,centerZ);
        //Box box = new Box( Vector3f.ZERO, 500,10,500);
        Geometry harborblock = new Geometry("Box", box);
        Material harbormat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        harbormat.setTexture("DiffuseMap", assetManager.loadTexture("Models/Harbor/Textures/concrete.jpg"));
        harborblock.setMaterial(harbormat);
        //harborblock.setLocalTranslation(-520, -10, 0);
        harborblock.setLocalTranslation(0, -10, 0);
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
        float roadWidth = 3f;
        
        Material roadmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        roadmat.setColor("Color", ColorRGBA.DarkGray);        
        Material nodemat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        nodemat.setColor("Color", ColorRGBA.Red);
        Material parkinglotmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        parkinglotmat.setColor("Color", ColorRGBA.Green);
        
        Box box = new Box( Vector3f.ZERO, 0.5f,0.5f,0.5f);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
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
        int nodei = 0;
        for(Pathfinding.Node n : Pathfinding.Pathfinder.Nodes){
            if(n!=null){
                Geometry road = new Geometry("Box", box);
                road.setMaterial(roadmat);

                road.setLocalTranslation(   n.getPosition().x, 
                                            n.getPosition().y+5, 
                                            n.getPosition().z);
                road.setMaterial(nodemat);
                rootNode.attachChild(road);

                BitmapText helloText = new BitmapText(guiFont, false);
                helloText.setSize(5);
                helloText.setText(nodei + "");
                helloText.setLocalTranslation(n.getPosition().x,n.getPosition().y+10,  n.getPosition().z);
                rootNode.attachChild(helloText);
            }
            nodei++;
        }
        for(Parkinglot.Parkinglot n : Pathfinding.Pathfinder.parkinglots){
            if(n!=null){
                Geometry road = new Geometry("Box", box);
                road.setMaterial(roadmat);

                road.setLocalTranslation(   n.node.getPosition().x, 
                                            n.node.getPosition().y+5, 
                                            n.node.getPosition().z);
                road.setMaterial(parkinglotmat);
                rootNode.attachChild(road);
            }
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

    
    /**
     * 
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        netlistener.readNetwork();
        
        objMgr.update(tpf);
        water.setLocalTranslation(cam.getLocation().x-5000, water.getLocalTranslation().y, cam.getLocation().z+5000);

        //Set vehicle volume
        if(objMgr.boatList.size()>0){
            float leastDistance_boat = 999999;
            float leastDistance_train = 999999;
            float leastDistance_truck = 999999;
            
            Helpers.Vector3f camPos = new Helpers.Vector3f(cam.getLocation().x, cam.getLocation().y, cam.getLocation().z);
            
            for(visualVehicle b : objMgr.boatList){
                float curDist = Helpers.Vector3f.distance(camPos, b.getPosition());
                
                if(b.GetVehicleType().equals(TransportVehicle.VehicleType.seaBoat)){
                    if(curDist<leastDistance_boat)leastDistance_boat = curDist;
                }
                else if(b.GetVehicleType().equals(TransportVehicle.VehicleType.train)){
                    if(curDist<leastDistance_train)leastDistance_train = curDist;
                }
                else if(b.GetVehicleType().equals(TransportVehicle.VehicleType.truck) ||
                        b.GetVehicleType().equals(TransportVehicle.VehicleType.AGV)){
                    if(curDist<leastDistance_truck)leastDistance_truck = curDist;
                }
            }

            
            //Set boat
            float maxVolume = 1f;
            float divider = 100f;
            leastDistance_boat/=divider;
            maxVolume-=leastDistance_boat;
            if(maxVolume<0)maxVolume=0;
            audio_boat.setVolume(maxVolume);
            
            //Set train
            maxVolume = 1f;
            divider = 100f;
            leastDistance_train/=divider;
            maxVolume-=leastDistance_train;
            if(maxVolume<0)maxVolume=0;
            audio_train.setVolume(maxVolume);
            
            //Set truck
            maxVolume = 1f;
            divider = 100f;
            leastDistance_truck/=divider;
            maxVolume-=leastDistance_truck;
            if(maxVolume<0)maxVolume=0;
            audio_truck.setVolume(maxVolume);
        }else{
            audio_boat.setVolume(0);
            audio_train.setVolume(0);
            audio_truck.setVolume(0);
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
