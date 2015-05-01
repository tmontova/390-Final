package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import java.util.ArrayList;

public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener{

    private BulletAppState jBullet;
    private RigidBodyControl base_phy, sides_phy, char1, char2;
    private CharacterControl eleph_phy;
    ArrayList<CharacterControl> char_control = new ArrayList<CharacterControl>();
    private boolean p1Left=false, p1Right=false, p1Forward=false, p1Back=false;
    private boolean p2Left=false, p2Right=false, p2Forward=false, p2Back=false;
    private boolean camera1 = false, camera2 = false;
    private Torus side;
    private Camera cam_2;
    private Node player1, player2;
    private GhostControl ghost1, ghost2;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        
        player1 = new Node("player1");
        player2 = new Node("player2");
        char1 = new RigidBodyControl(1f);
        char2 = new RigidBodyControl(1f);
        cameraSetUp(1);
        
        setUpKeys();
        jBullet = new BulletAppState();
        stateManager.attach(jBullet);
        jBullet.getPhysicsSpace().addCollisionListener(this);
        
        arenaSetUp();
        playerSetUp(new Vector3f(6.5f, 0, 0), player1, ghost1, char1, 0);
        playerSetUp(new Vector3f(0f, 0, 6.5f), player2, ghost2, char2, 1);
        setUpLight();
        for(int i = 0; i < 10; i++){
            addBall();
        }
         
    }
    private void cameraSetUp(int i){
        CameraNode camNode1 = null;
        CameraNode camNode2 = null;
        if (i == 0) {
            cam.setLocation(new Vector3f(0, 12, 12));
            cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
            cam_2 = cam.clone();
            cam.setViewPort(0.0f, 1.0f, 0.5f, 1.0f);
            cam_2.setViewPort(0.0f, 1.0f, 0.0f, 0.5f);
            ViewPort vp2 = renderManager.createMainView("View of cam_2", cam_2);
            vp2.attachScene(rootNode);
            vp2.setClearFlags(true, true, true);
            vp2.setBackgroundColor(ColorRGBA.DarkGray);

            camNode1 = new CameraNode("CamNode1", cam);
            camNode1.setControlDir(ControlDirection.SpatialToCamera);
            camNode1.setLocalTranslation(new Vector3f(1.5f, 5, 0));
            camNode1.lookAt(new Vector3f(-4, 0, 0), Vector3f.UNIT_Y);

            camNode2 = new CameraNode("CamNode2", cam_2);
            camNode2.setControlDir(ControlDirection.SpatialToCamera);
            camNode2.setLocalTranslation(new Vector3f(1.5f, 5, 0));
            camNode2.lookAt(new Vector3f(-4, 0, 0), Vector3f.UNIT_Y);

            player1.attachChild(camNode1);
            player2.attachChild(camNode2);

        }
        else {
            try {
                player1.detachChild(camNode1);
                player2.detachChild(camNode2);
                cam_2.setViewPort(0.0f, 0.0f, 0.0f, 0.0f);
            }
            catch(Exception e){
                System.out.println("Cam nodes not attached");
            }
            viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
            cam.setLocation(new Vector3f(0, 12, 12));
            cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
            cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
        }
            flyCam.setEnabled(false);
    }
    
    private void addBall(){
        Sphere s = new Sphere(16, 16, 0.2f);
        Geometry ball_geo = new Geometry("Ball", s);
        ball_geo.setLocalTranslation(new Vector3f(0, 2f, 0f));
        rootNode.attachChild(ball_geo);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        ball_geo.setMaterial(mat2);
        
        RigidBodyControl ball_phy = new RigidBodyControl(5f);
        ball_geo.addControl(ball_phy);
        ball_phy.setFriction(0.1f);
        ball_phy.setRestitution(1);
        jBullet.getPhysicsSpace().add(ball_phy);
    }
    
    private void arenaSetUp(){

        Box base = new Box(10, 2, 10);
        Torus sides = new Torus(16, 16, 0.5f, 6);
        side = sides;
        Geometry base_geo = new Geometry("base", base);
        Geometry sides_geo = new Geometry("side", sides);

        sides_geo.rotate(1.6f, 0, 0);
        sides_geo.setLocalTranslation(0, 2, 0);
        Material baseMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseMat.setTexture("ColorMap", assetManager.loadTexture("Textures/brick.jpg"));        
        
        Material sidesMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sidesMat.setTexture("ColorMap", assetManager.loadTexture("Textures/wood.jpg"));
        
        base_geo.setMaterial(baseMat);
        sides_geo.setMaterial(sidesMat);

        base_phy = new RigidBodyControl(0f);
        sides_phy = new RigidBodyControl(0f);
        base_geo.addControl(base_phy);
        sides_geo.addControl(sides_phy);
        base_phy.setFriction(0.1f);
        base_phy.setRestitution(0);
        sides_phy.setRestitution(1);
        jBullet.getPhysicsSpace().add(sides_phy);
        jBullet.getPhysicsSpace().add(base_phy);

        rootNode.attachChild(base_geo);
        rootNode.attachChild(sides_geo);
    }
    
    private void playerSetUp(Vector3f loc, Node player, GhostControl ghost, RigidBodyControl rbc, int i){
        Spatial eleph = assetManager.loadModel("Models/e1.obj");

        CapsuleCollisionShape capsule = new CapsuleCollisionShape(0.1f, 0.1f);
        eleph_phy = new CharacterControl(capsule, .01f);
        
 
        Material eleph_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        if(i == 0){
            eleph_mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/blue.jpg"));
        }
        else{
            eleph_mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/pink.jpg"));
        }
        eleph.setMaterial(eleph_mat);
        eleph.setLocalScale(0.5f);
        

        eleph_phy.addCollideWithGroup(1);
        eleph_phy.setCollisionGroup(1);
        
        ghost = new GhostControl(new BoxCollisionShape(new Vector3f(1,1,1)));
        player.addControl(rbc);
        player.addControl(ghost);
        
        player.attachChild(eleph);
        player.addControl(eleph_phy);
        
        eleph_phy.setPhysicsLocation(loc);
        
        BetterCharacterControl bcc = new BetterCharacterControl(1.5f, 1.5f, 1.5f);
        jBullet.getPhysicsSpace().add(eleph_phy);
        jBullet.getPhysicsSpace().add(ghost);
        rootNode.attachChild(player);
        char_control.add(eleph_phy);
    }
    
    
    
    private void setUpLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
 
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    private void setUpKeys() {
        inputManager.addMapping("p1Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("p1Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("p1Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("p1Back", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(this, "p1Left");
        inputManager.addListener(this, "p1Right");
        inputManager.addListener(this, "p1Right");
        inputManager.addListener(this, "p1Forward");
        inputManager.addListener(this, "p1Back");
        inputManager.addMapping("p2Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("p2Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("p2Forward", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("p2Back", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addListener(this, "p2Left");
        inputManager.addListener(this, "p2Right");
        inputManager.addListener(this, "p2Right");
        inputManager.addListener(this, "p2Forward");
        inputManager.addListener(this, "p2Back");
        inputManager.addMapping("camera1", new KeyTrigger(KeyInput.KEY_9));
        inputManager.addListener(this, "camera1");        
        inputManager.addMapping("camera2", new KeyTrigger(KeyInput.KEY_0));
        inputManager.addListener(this, "camera2");
        
    }
    
    private double move(double rot, Vector3f pos, float radius, CharacterControl c, boolean right){
        if(right){
            rot-=.005;
        }
        else{
            rot+=.005;
        }
        float x = (float) (radius*Math.cos(rot));
        float y = pos.y;
        float z = (float) (radius*Math.sin(rot));
        c.setPhysicsLocation(new Vector3f(x, y, z));
        Vector3f view = new Vector3f(-pos.z, 0, pos.x);
        view.normalize();
        c.setViewDirection(view);
        return rot;
    }

    private double rotationP1 = 0;
    private double rotationP2 = Math.PI;
    @Override
    public void simpleUpdate(float tpf) {
        float radius = side.getOuterRadius() + .5f;
        Vector3f pos1 = char_control.get(0).getPhysicsLocation();
        Vector3f pos2 = char_control.get(1).getPhysicsLocation();
        

        if (p1Left) {
            if (!isColRight(pos1, pos2, 3.0f)) {
                rotationP1 = move(rotationP1, pos1, radius, char_control.get(0), false) % (2 * Math.PI);
            }
        }
        if (p1Right) {
            if (!isColLeft(pos1, pos2, 0.2f)) {
                rotationP1 = move(rotationP1, pos1, radius, char_control.get(0), true) % (2 * Math.PI);
            }
        }
        if (p2Left) {
            if (!isColRight(pos1, pos2, 0.2f)) {
                rotationP2 = move(rotationP2, pos2, radius, char_control.get(1), false) % (2 * Math.PI);
            }
        }
        if (p2Right) {
            if (!isColLeft(pos1, pos2, 0.2f)) {
                rotationP2 = move(rotationP2, pos2, radius, char_control.get(1), true) % (2 * Math.PI);
            }
        }
        if (camera1) {
            cameraSetUp(0);
        }
        if (camera2) {
            cameraSetUp(1);
        }
    }
    
    private boolean isColRight(Vector3f p1, Vector3f p2, float tol){
        if(p1.x > p2.x &&  p1.z > p2.z && p1.x - p2.x < tol && p1.z - p2.z < tol){
            return true;
        }
        else if(p1.x < p2.x &&  p1.z < p2.z && p2.x - p1.x < tol && p2.z - p1.z < tol){
            return true;
        }
        return false;
    }
    private boolean isColLeft(Vector3f p1, Vector3f p2, float tol){
        if(p1.x > p2.x &&  p1.z < p2.z && p1.x - p2.x < tol && p2.z - p1.z < tol){
            return true;
        }
        else if(p1.x < p2.x &&  p1.z > p2.z && p2.x - p1.x < tol && p1.z - p2.z < tol){
            return true;
        }
        return false;
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals("p1Left")){
            p1Left = isPressed;
        }        
        if(name.equals("p1Right")){
            p1Right = isPressed;
        }        
        if(name.equals("p1Forward")){
            p1Forward = isPressed;
        }        
        if(name.equals("p1Back")){
            p1Back = isPressed;
        } 
        if(name.equals("p2Left")){
            p2Left = isPressed;
        }        
        if(name.equals("p2Right")){
            p2Right = isPressed;
        }        
        if(name.equals("p2Forward")){
            p2Forward = isPressed;
        }        
        if(name.equals("p2Back")){
            p2Back = isPressed;
        }
        if(name.equals("camera1")){
            camera1 = isPressed;
        }
        if(name.equals("camera2")){
            camera2 = isPressed;
        }
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
//        if(event.getNodeA().getName().equals("player2")){
//                if(event.getNodeB().getName().equals("player1")){
////                    System.out.println("Ball has been caught");
//                                    System.out.println(event.getNodeA()+" "+event.getNodeB());
//                                   
//                }
//
//        }
        if(event.getNodeA().getName().equals("player1")){
                if(event.getNodeB().getName().equals("player2")){
//                    System.out.println("Ball has been caught");
                                    System.out.println(event.getNodeA().getLocalTranslation()+" "+event.getNodeB().getLocalTranslation());

                }

        }
        else{
            System.out.println("No collision");
        }
//        try{
//        System.out.println(ghost1.getOverlappingCount()+" "+ghost1.getOverlappingObjects());
//        }
//        catch(Exception e){
//        }
    }
}
