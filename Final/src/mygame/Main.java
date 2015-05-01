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
import com.jme3.bullet.control.BetterCharacterControl;
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
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
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
    private Torus side;
    private Camera cam_2;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        cam.setLocation(new Vector3f(0,12,12));
        cam.lookAt(new Vector3f(0,0,0), Vector3f.UNIT_Y);
        cam_2 = cam.clone();
        cam.setViewPort(0.0f, 1.0f, 0.5f, 1.0f);
        cam_2.setViewPort(0.0f, 1.0f, 0.0f, 0.5f);
        ViewPort vp2 = renderManager.createMainView("View of cam_2", cam_2);
        vp2.attachScene(rootNode);
        vp2.setClearFlags(true, true, true);
        vp2.setBackgroundColor(ColorRGBA.DarkGray);
        
        flyCam.setEnabled(false);
        
        
        setUpKeys();
        jBullet = new BulletAppState();
        stateManager.attach(jBullet);
        jBullet.getPhysicsSpace().addCollisionListener(this);
        
        arenaSetUp();
        playerSetUp(new Vector3f(6, 0, 0));
        playerSetUp(new Vector3f(6, 0 ,0));
        setUpLight();
        for(int i = 0; i < 10; i++){
            addBall();
        }
         
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
    
    private void playerSetUp(Vector3f loc){
        Spatial eleph = assetManager.loadModel("Models/e1.obj");

        CapsuleCollisionShape capsule = new CapsuleCollisionShape(0.5f, 0.5f);
        eleph_phy = new CharacterControl(capsule, .01f);
        
        Node player = new Node("player");
 
        Material eleph_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        eleph_mat.setColor("Diffuse", ColorRGBA.Blue);
        eleph.setMaterial(eleph_mat);
        eleph.setLocalScale(0.5f);
        
        player.attachChild(eleph);
        player.addControl(eleph_phy);
        eleph_phy.setPhysicsLocation(loc);
        
        BetterCharacterControl bcc = new BetterCharacterControl(1.5f, 1.5f, 1.5f);
        jBullet.getPhysicsSpace().add(eleph_phy);
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
        
    }

    private double rotationP1 = 0;
    private double rotationP2 = 0;
    @Override
    public void simpleUpdate(float tpf) {
        float radius = side.getOuterRadius();
        float x, y, z;


        if(p1Left || p1Right || p1Forward || p1Back){
            Vector3f pos = char_control.get(0).getPhysicsLocation();
            if(p1Left){
                rotationP1+=.005;
                x = (float) (radius*Math.cos(rotationP1));
                y = pos.y;
                z = (float) (radius*Math.sin(rotationP1));
                char_control.get(0).setPhysicsLocation(new Vector3f(x, y, z));
//                System.out.println(char_control.get(0).getViewDirection());
                Vector3f view = new Vector3f(-pos.z, 0, pos.x);
                view.normalize();
                char_control.get(0).setViewDirection(view);

            }
            if(p1Right){
                rotationP1-=.005;
                x = (float) (radius*Math.cos(rotationP1));
                y = pos.y;
                z = (float) (radius*Math.sin(rotationP1));
                char_control.get(0).setPhysicsLocation(new Vector3f(x, y, z));
//                System.out.println(char_control.get(0).getViewDirection());
                Vector3f view = new Vector3f(-pos.z, 0, pos.x);
                view.normalize();
                char_control.get(0).setViewDirection(view);
            }
            if(p1Forward){
//                char_control.get(0).setPhysicsLocation(new Vector3f(pos.x, pos.y, pos.z-.01f));
            }
            if(p1Back){
//                char_control.get(0).setPhysicsLocation(new Vector3f(pos.x, pos.y, pos.z+.01f));
            }
        }
        else if(p2Left || p2Right || p2Forward || p2Back){
            if(char_control.size() > 1){
                Vector3f pos = char_control.get(1).getPhysicsLocation();
                if(p2Left){
                    rotationP2+=.005;
                    x = (float) (radius*Math.cos(rotationP2));
                    y = pos.y;
                    z = (float) (radius*Math.sin(rotationP2));
                    char_control.get(1).setPhysicsLocation(new Vector3f(x, y, z));
//                    System.out.println(char_control.get(1).getViewDirection());
                    Vector3f view = new Vector3f(-pos.z, 0, pos.x);
                    view.normalize();
                    char_control.get(1).setViewDirection(view);

                }
                if(p2Right){
                    rotationP2-=.005;
                    x = (float) (radius*Math.cos(rotationP2));
                    y = pos.y;
                    z = (float) (radius*Math.sin(rotationP2));
                    char_control.get(1).setPhysicsLocation(new Vector3f(x, y, z));
//                    System.out.println(char_control.get(1).getViewDirection());
                    Vector3f view = new Vector3f(-pos.z, 0, pos.x);
                    view.normalize();
                    char_control.get(1).setViewDirection(view);
                }
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
        System.out.println(p2Left+" "+p1Left);
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().getName().equals("Ball")){
//                System.out.println(event.getNodeA()+" "+event.getNodeB());
                if(event.getNodeB().getName().equals("player")){
//                    System.out.println("Ball has been caught");
                }

        }
        if(event.getNodeA().getName().equals("player")){
//                System.out.println(event.getNodeA()+" "+event.getNodeB());
                if(event.getNodeB().getName().equals("Ball")){
//                    System.out.println("Ball has been caught");
                }

        }
    }
}
