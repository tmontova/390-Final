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
import java.util.ArrayList;

public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener{

    private BulletAppState jBullet;
    private RigidBodyControl base_phy, sides_phy;
    private CharacterControl eleph_phy;
    ArrayList<CharacterControl> char_control = new ArrayList<CharacterControl>();
    private boolean p1Left=false, p1Right=false, p1Forward=false, p1Back=false;
    private boolean p2Left=false, p2Right=false, p2Forward=false, p2Back=false;
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

        
        arenaSetUp();
        playerSetUp();
        playerSetUp();
        setUpLight();
        for(int i = 0; i < 10; i++){
            addBall();
        }
         
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
        char_control.add(eleph_phy);
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
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f pos;

        if(p1Left || p1Right || p1Forward || p1Back){
            System.out.println("SimpleUpdate");
            pos = char_control.get(0).getPhysicsLocation();
            if(p1Left){
                char_control.get(0).setPhysicsLocation(new Vector3f(pos.x-.01f, pos.y, pos.z));
            }
            if(p1Right){
                char_control.get(0).setPhysicsLocation(new Vector3f(pos.x+.01f, pos.y, pos.z));
            }
            if(p1Forward){
                char_control.get(0).setPhysicsLocation(new Vector3f(pos.x, pos.y, pos.z-.01f));
            }
            if(p1Back){
                char_control.get(0).setPhysicsLocation(new Vector3f(pos.x, pos.y, pos.z+.01f));
            }
        }
        else if(p2Left || p2Right || p2Forward || p2Back){
            pos = char_control.get(1).getPhysicsLocation();
            if(p2Left){
                char_control.get(1).setPhysicsLocation(new Vector3f(pos.x-.01f, pos.y, pos.z));
            }
            if(p2Right){
                char_control.get(1).setPhysicsLocation(new Vector3f(pos.x+.01f, pos.y, pos.z));
            }
            if(p2Forward){
                char_control.get(1).setPhysicsLocation(new Vector3f(pos.x, pos.y, pos.z-.01f));
            }
            if(p2Back){
                char_control.get(1).setPhysicsLocation(new Vector3f(pos.x, pos.y, pos.z+.01f));
            }
        }
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
