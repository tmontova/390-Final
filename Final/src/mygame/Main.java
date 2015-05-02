package mygame;
 
import com.jme3.app.SimpleApplication;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
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
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
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


public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener, AnimEventListener{

    private BulletAppState jBullet;
    private RigidBodyControl base_phy, sides_phy,center_phy, char1, char2;
    private CharacterControl eleph_phy;
    ArrayList<CharacterControl> char_control = new ArrayList<CharacterControl>();
    private boolean p1Left=false, p1Right=false, p1Forward=false, p1Back=false;
    private boolean p2Left=false, p2Right=false, p2Forward=false, p2Back=false;
    private boolean camera1 = false, camera2 = false;
    private Torus side;
    private Camera cam_2;
    private Node player1, player2;
    private GhostControl ghost1, ghost2;
    private int p1Score = 0, p2Score = 0;
    BitmapText hud;
    private AnimChannel channel1, channel2;
    private AnimControl control1, control2;

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
        
        hud = new BitmapText(guiFont, false);
        hud.setSize(guiFont.getCharSet().getRenderedSize());
        hud.setColor(ColorRGBA.Blue);
        hud.setText("Blue: 0, Red: 0");
        hud.setLocalTranslation(0, 600, 0);
        guiNode.attachChild(hud);
        
        setUpKeys();
        jBullet = new BulletAppState();
        stateManager.attach(jBullet);
        jBullet.getPhysicsSpace().addCollisionListener(this);
        
        arenaSetUp();
        playerSetUp(new Vector3f(6.5f, 0, 0), player1, ghost1, char1, 0);
        playerSetUp(new Vector3f(0f, 0, 6.5f), player2, ghost2, char2, 1);
        setUpLight();
        for(int i = 0; i < 10; i++){
            float randx = (float) Math.floor(Math.random()*3);
            float randz = (float) Math.floor(Math.random()*3);
            if(Math.random() > .5){
                randx = randx*-1;
            }
            if(Math.random() > .5){
                randz = randz*-1;
            }
            addBall(new Vector3f(randx, 3.5f, randz));
        }
         
    }
    private void cameraSetUp(int i){
        CameraNode camNode1;
        CameraNode camNode2;
        if (i == 0) {
            cam.setLocation(new Vector3f(0, 12, 12));
            cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
            cam_2 = cam.clone();
            cam.setViewPort(0.0f, 1.0f, 0.50f, 1.0f);
            cam_2.setViewPort(0.0f, 1.0f, 0.0f, 0.50f);
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

            viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
            cam.setLocation(new Vector3f(0, 12, 12));
            cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
            cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
        }
            flyCam.setEnabled(false);
    }
    
    private void addBall(Vector3f loc){
        Sphere s = new Sphere(16, 16, 0.2f);
        Geometry ball_geo = new Geometry("Ball", s);
        ball_geo.setLocalTranslation(loc);
        rootNode.attachChild(ball_geo);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setTexture("DiffuseMap", assetManager.loadTexture("Textures/soccer.jpg"));
        ball_geo.setMaterial(mat2);
        
        RigidBodyControl ball_phy = new RigidBodyControl(5f);
        ball_geo.addControl(ball_phy);
        ball_phy.setFriction(0.001f);
        ball_phy.setRestitution(1);
        jBullet.getPhysicsSpace().add(ball_phy);
    }
    
    private void arenaSetUp(){

        Box base = new Box(10, 2, 10);
        Torus sides = new Torus(16, 16, 0.5f, 6);
        Sphere sphere = new Sphere(16,16,6);
        side = sides;
        Geometry base_geo = new Geometry("base", base);
        Geometry sides_geo = new Geometry("side", sides);
        Geometry sphere_geo = new Geometry("center", sphere);

        sides_geo.rotate(1.6f, 0, 0);
        sides_geo.setLocalTranslation(0, 2, 0);
        sphere_geo.setLocalTranslation(0, -2.5f, 0);
        
        Material baseMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/grass.jpg"));        
        Material centerMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        centerMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/iron.jpg")); 
        Material sidesMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        sidesMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/wood.jpg"));
        
        base_geo.setMaterial(baseMat);
        sides_geo.setMaterial(sidesMat);
        sphere_geo.setMaterial(centerMat);

        base_phy = new RigidBodyControl(0f);
        sides_phy = new RigidBodyControl(0f);
        center_phy = new RigidBodyControl(0f);
        base_geo.addControl(base_phy);
        sides_geo.addControl(sides_phy);
        sphere_geo.addControl(center_phy);
        base_phy.setFriction(0f);
        base_phy.setRestitution(0);
        center_phy.setRestitution(0);
        center_phy.setFriction(0f);
        sides_phy.setRestitution(1);
        jBullet.getPhysicsSpace().add(center_phy);
        jBullet.getPhysicsSpace().add(sides_phy);
        jBullet.getPhysicsSpace().add(base_phy);

        rootNode.attachChild(sphere_geo);
        rootNode.attachChild(base_geo);
        rootNode.attachChild(sides_geo);
    }
    
    private void playerSetUp(Vector3f loc, Node player, GhostControl ghost, RigidBodyControl rbc, int i){
        Spatial eleph = assetManager.loadModel("Models/Sphere.mesh.xml");
        
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
        
        ghost = new GhostControl(new BoxCollisionShape(new Vector3f(1.5f,1,1.5f)));
        player.addControl(rbc);
        player.addControl(ghost);
        
        player.attachChild(eleph);
        player.addControl(eleph_phy);
        
        eleph_phy.setPhysicsLocation(loc);
        Vector3f view = new Vector3f(-loc.z, 0, loc.x);
        view.normalize();
        eleph_phy.setViewDirection(view);
        
        jBullet.getPhysicsSpace().add(eleph_phy);
        jBullet.getPhysicsSpace().add(ghost);
        rootNode.attachChild(player);
        char_control.add(eleph_phy);

        if (i == 0) {
            control1 = player.getChild(0).getControl(AnimControl.class);
            control1.addListener(this);
            channel1 = control1.createChannel();
            channel1.setAnim("Stand");
        } else {
            control2 = player.getChild(0).getControl(AnimControl.class);
            control2.addListener(this);
            channel2 = control2.createChannel();
            channel2.setAnim("Stand");
        }
    }
    
    
    
    private void setUpLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White);

        rootNode.addLight(al);

        DirectionalLight light = new DirectionalLight();
        light.setColor(ColorRGBA.White);
        light.setDirection(new Vector3f(5, 0, -5).normalize());
        rootNode.addLight(light);

        PointLight light2 = new PointLight();
        light2.setColor(ColorRGBA.Blue);
        light2.setRadius(5f);
        light2.setPosition(new Vector3f(0, 4, 0));
        rootNode.addLight(light2);
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
    private double rotationP2 = Math.PI/2;
    @Override
    public void simpleUpdate(float tpf) {
        float radius = side.getOuterRadius() + .5f;
        Vector3f pos1 = char_control.get(0).getPhysicsLocation();
        Vector3f pos2 = char_control.get(1).getPhysicsLocation();


        if (p1Left) {
            rotationP1 = move(rotationP1, pos1, radius, char_control.get(0), false) % (2 * Math.PI);
            channel1.setAnim("Waddle", 2f);
            channel1.setLoopMode(LoopMode.Loop);

        }
        if (p1Right) {
            rotationP1 = move(rotationP1, pos1, radius, char_control.get(0), true) % (2 * Math.PI);
            channel1.setAnim("Waddle", 2f);
            channel1.setLoopMode(LoopMode.Loop);
        }
        if (p2Left) {
            rotationP2 = move(rotationP2, pos2, radius, char_control.get(1), false) % (2 * Math.PI);
            channel2.setAnim("Waddle", 2f);
            channel2.setLoopMode(LoopMode.Loop);
        }
        if (p2Right) {
            rotationP2 = move(rotationP2, pos2, radius, char_control.get(1), true) % (2 * Math.PI);
            channel2.setAnim("Waddle", 2f);
            channel2.setLoopMode(LoopMode.Loop);
        }
        if (camera1) {
            cameraSetUp(0);
        }
        if (camera2) {
            cameraSetUp(1);
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
        if(name.equals("camera1")){
            camera1 = isPressed;
        }
        if(name.equals("camera2")){
            camera2 = isPressed;
        }
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA().getName().equals("player2")) {
            if (event.getNodeB().getName().equals("Ball")) {
                RigidBodyControl c = (RigidBodyControl) event.getNodeB().getControl(0);
                c.setEnabled(false);
                rootNode.detachChild(event.getNodeB());
                p2Score++;
                hud.setText("Blue: " + p1Score + ", Red: " + p2Score);
            }
        }
        if (event.getNodeA().getName().equals("player1")) {
            if (event.getNodeB().getName().equals("Ball")) {
                RigidBodyControl c = (RigidBodyControl) event.getNodeB().getControl(0);
                c.setEnabled(false);
                rootNode.detachChild(event.getNodeB());
                p1Score++;
                hud.setText("Blue: " + p1Score + ", Red: " + p2Score);
            }
        }
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
      if (animName.equals("Waddle")) {
        channel.setAnim("Stand", 0.50f);
        channel.setLoopMode(LoopMode.DontLoop);
        channel.setSpeed(1f);
      }
      if (animName.equals("Trunk")) {
        channel.setAnim("Stand", 0.50f);
        channel.setLoopMode(LoopMode.DontLoop);
        channel.setSpeed(1f);
      }
    }
 
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {}

}
