package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
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
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;

public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener{

    private BulletAppState jBullet;
    private RigidBodyControl arena;
//    private RigidBodyControl ball_phy;
    private RigidBodyControl base_phy, sides_phy;
    private RigidBodyControl eleph_body;
    private CharacterControl eleph_phy;
//    private Geometry ball_geo;
    private boolean left=false, right=false, forward=false, back=false;
    private Node player;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        cam.setLocation(new Vector3f(0,10,10));
        cam.lookAt(new Vector3f(0,0,0), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        setUpKeys();
        jBullet = new BulletAppState();
        stateManager.attach(jBullet);
        
        jBullet.getPhysicsSpace().addCollisionListener(this);

        
        //arena set up
//        sceneModel = assetManager.loadModel("Models/arena.scene");
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
//        sceneModel.setMaterial(mat);
//        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node)sceneModel);
//        arena = new RigidBodyControl(sceneShape, 0);
//        sceneModel.addControl(arena);   
//        rootNode.attachChild(sceneModel);
        arenaSetUp();
        playerSetUp();
        setUpLight();
        for(int i = 0; i < 10; i++){
            addBall();
        }
        

  
//        jBullet.getPhysicsSpace().add(arena);
//        jBullet.getPhysicsSpace().add(ball);  
    }
    
    private void addBall(){
        Sphere s = new Sphere(16, 16, 0.2f);
        Geometry ball_geo = new Geometry("Ball", s);
        ball_geo.setLocalTranslation(new Vector3f(0, 2f, 0f));
//        ball_geo.setLocalScale(0.2f);
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

        Box base = new Box(5, 2, 5);
        Torus sides = new Torus(16, 16, 1.5f, 6);
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
        sides_phy.setRestitution(1);
        jBullet.getPhysicsSpace().add(sides_phy);
        jBullet.getPhysicsSpace().add(base_phy);

        rootNode.attachChild(base_geo);
        rootNode.attachChild(sides_geo);
    }
    
    private void playerSetUp(){
        Spatial eleph = assetManager.loadModel("Models/eleph2.obj");
//        Box body = new Box(.5f,.5f, .5f);
//        Box top = new Box(.2f, .1f, .5f);
//        Box bottom = new Box(.2f, .01f, .5f);
//        
//        Geometry body_geo = new Geometry("Box", body);
//        Geometry top_geo = new Geometry("Box", top);
//        Geometry bottom_geo = new Geometry("Box", bottom);
//        
//        top_geo.setLocalTranslation(.7f, .5f, 0);
//        bottom_geo.setLocalTranslation(.7f, .49f, 0);
//        CollisionShape s = CollisionShapeFactory.createMeshShape(eleph);
//        CompoundCollisionShape capsule = new CompoundCollisionShape();
//        capsule.addChildShape(s, Vector3f.ZERO);
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(0.5f, 0.5f);
        eleph_phy = new CharacterControl(capsule, .01f);
        player = new Node("player");
 
        Material eleph_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        eleph_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/elephant.jpg"));
//        body_geo.setMaterial(eleph_mat);
//        top_geo.setMaterial(eleph_mat);
//        bottom_geo.setMaterial(eleph_mat);
        eleph.setMaterial(eleph_mat);
        eleph.setLocalScale(2.5f);
        
        player.addControl(eleph_phy);
        player.attachChild(eleph);
//        player.attachChild(body_geo);
//        player.attachChild(top_geo);
//        player.attachChild(bottom_geo);
        eleph_phy.setGravity(30);
//        eleph_phy.setPhysicsLocation(new Vector3f(0,0.5f,0));
        jBullet.getPhysicsSpace().add(eleph_phy);
        
        rootNode.attachChild(player);
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
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Forward");
        inputManager.addListener(this, "Back");
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f pos = eleph_phy.getPhysicsLocation();
        
        if(left){
            eleph_phy.setPhysicsLocation(new Vector3f(pos.x-.01f, pos.y, pos.z));
        }
        if(right){
            eleph_phy.setPhysicsLocation(new Vector3f(pos.x+.01f, pos.y, pos.z));
        }
        if(forward){
            eleph_phy.setPhysicsLocation(new Vector3f(pos.x, pos.y, pos.z-.01f));
        }
        if(back){
            eleph_phy.setPhysicsLocation(new Vector3f(pos.x, pos.y, pos.z+.01f));
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals("Left")){
            left = isPressed;
        }        
        if(name.equals("Right")){
            right = isPressed;
        }        
        if(name.equals("Forward")){
            forward = isPressed;
        }        
        if(name.equals("Back")){
            back = isPressed;
        }    
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().getName().equals("Ball")){
                System.out.println(event.getNodeA()+" "+event.getNodeB());
                if(event.getNodeB().getName().equals("player")){
                    System.out.println("Ball has been caught");
                }

        }
        if(event.getNodeA().getName().equals("player")){
                System.out.println(event.getNodeA()+" "+event.getNodeB());
                if(event.getNodeB().getName().equals("Ball")){
                    System.out.println("Ball has been caught");
                }

        }
    }
}
