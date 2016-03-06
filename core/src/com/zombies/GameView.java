package com.zombies;

import com.HUD.*;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.GL20;
import com.data.Stats;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class GameView implements Screen {

    public static GameView gv;
    public static ArrayList<ArrayList<Zone>> zones = new ArrayList<ArrayList<Zone>>();
    private static ArrayList<Zombie> activeZombies = new ArrayList<Zombie>();
    public static FontGen fontGen;
    public static Stats stats = new Stats();

    //brought down a level
    private PerspectiveCamera cam;
    private SpriteBatch HUDSpriteBatch;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    //regular variables
    protected Player player;
    protected World world;
    protected Zombies main;
    protected Box grid[][];

    protected Random random = new Random();
    protected CameraHandle camHandle;
    protected Meshes meshes = new Meshes();
    protected int lightingCount = 0;
    protected ThumbpadLeft thumbpadLeft;
    protected ThumbpadRight thumbpadRight;
    protected ShootButton shootButton;
    public MessageHandler mh;
    private HUD hud;
    private LinkedList<DebugDots> debugDots = new LinkedList<DebugDots>();
    private int frame = 0;

    public float scale = 1;

    private Box2DDebugRenderer debugRenderer;
    private Matrix4 debugMatrix;

    //lists
    LinkedList<PostponedZombie> postZombie = new LinkedList<PostponedZombie>();
    LinkedList<PostponedZombie> postZombieDump = new LinkedList<PostponedZombie>();
    LinkedList<DyingZombie> dyingZombie = new LinkedList<DyingZombie>();
    LinkedList<DyingZombie> dyingZombieDump = new LinkedList<DyingZombie>();

    public GameView() {
        this.gv = this;
        this.main = Zombies.main;

        fontGen = new FontGen();
        hud = new HUD();
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
        HUDSpriteBatch = new SpriteBatch();
        spriteBatch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();

        grid = new Box[C.GRID_WIDTH + 1][C.GRID_HEIGHT + 1];
        world = new World(new Vector2(), true);
        setReferences();
        generateLevel();
        addSurvivors();
        populateLevel();
        player = new Player(grid[1][1]);
        camHandle = new CameraHandle(this);
        int radius = (int)(Gdx.graphics.getWidth() * C.JOY_SIZE);
        thumbpadLeft = new ThumbpadLeft(this);
        thumbpadRight = new ThumbpadRight(this);
        shootButton = new ShootButton(this);
        mh = new MessageHandler(this);
        //meshes.main.play();

        Gdx.input.setInputProcessor(hud);
    }

    private void addSurvivors() {
        if (!C.POPULATE_SURVIVORS) return;
        for (int i=1;i<=10;i++) {
            randomBox().addSurvivor();
        }
    }

    public ShootButton getShootButton() {
        return shootButton;
    }

    public com.HUD.HUD getHUD() {
        return hud;
    }

    public void addMessage(Message m) {
        mh.addMessage(m);
    }

    public Zombies getMain() {
        return main;
    }

    public ThumbpadLeft getThumbpadLeft() {
        return thumbpadLeft;
    }
    public ThumbpadRight getThumbpadRight() { return thumbpadRight;}

    public void populateLevel() {
        for (int i = 1; i <= C.GRID_WIDTH; i++){
            for (int j = 1; j <= C.GRID_HEIGHT; j++){
                if (random.nextFloat() < 0.6f) {
                    for (int k=0; k<= random.nextInt(C.BOX_MAX_ZOMBIES) + 2; k++) {
                        grid[i][j].addZombie();
                        stats.numZombies ++;
                    }
                }
            }
        }
    }

    public Box randomBox(){
        return grid[random.nextInt(C.GRID_WIDTH)+1][random.nextInt(C.GRID_HEIGHT)+1];
    }

    protected void generateLevel() {
        for (int i = 1; i <= C.GRID_HEIGHT; i++){
            for (int j = 1; j <= C.GRID_WIDTH; j++){
                if (!grid[j][i].isTouched()){
                    new Room(grid[j][i]);
                }
            }
        }
        for (int i = 1; i <= C.GRID_HEIGHT; i++){
            for (int j = 1; j <= C.GRID_WIDTH; j++){
                if (!grid[j][i].isPathed()) {
                    grid[j][i].path(C.PATH_LENGTH);
                }
            }
        }
        for (int i = 1; i <= C.GRID_HEIGHT;i++) {
            for (int j = 1; j <= C.GRID_WIDTH; j++) {
                grid[j][i].getRoom().findAdjacentRooms();
            }
        }
    }

    public int getLightingCount() {
        return lightingCount;
    }

    public Meshes getMeshes() {
        return meshes;
    }

    public Box[][] getGrid() {
        return grid;
    }

    protected void setReferences(){
        for (int i = 1; i <= C.GRID_WIDTH; i++){
            for (int j = 1; j <= C.GRID_HEIGHT; j++){
                grid[i][j] = new Box((i - 1) * C.BOX_WIDTH, (j - 1) * C.BOX_HEIGHT, i, j);
            }
        }
        for (int i = 1; i <= C.GRID_WIDTH; i++){
            for (int j = 1; j <= C.GRID_HEIGHT; j++){
                if (j > 1){
                    grid[i][j].addAdjBox(grid[i][j - 1]);
                }
                else {
                    grid[i][j].addAdjBox(null);
                }
                if (i < C.GRID_WIDTH){
                    grid[i][j].addAdjBox(grid[i + 1][j]);
                }
                else {
                    grid[i][j].addAdjBox(null);
                }
                if (j < C.GRID_HEIGHT){
                    grid[i][j].addAdjBox(grid[i][j + 1]);
                }
                else {
                    grid[i][j].addAdjBox(null);
                }
                if (i > 1){
                    grid[i][j].addAdjBox(grid[i - 1][j]);
                }
                else {
                    grid[i][j].addAdjBox(null);
                }
            }
        }
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public void addPostZombie(PostponedZombie z) {
        postZombie.add(z);
    }

    public void addDyingZombie(DyingZombie z) {
        dyingZombie.add(z);
    }

    protected void clearDumps() {
        for (PostponedZombie z: postZombieDump) {
            postZombie.remove(z);
        }
        for (DyingZombie z: dyingZombieDump) {
            dyingZombie.remove(z);
        }
        postZombieDump.clear();
        dyingZombieDump.clear();
    }

    public void dumpPostZombie(PostponedZombie z) {
        postZombieDump.add(z);
    }

    public void dumpDyingZombie(DyingZombie z) {
        dyingZombieDump.add(z);
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void render(float dt) {
        updateLoop();

        handleContacts();
        camHandle.update(dt);

        lightingCount ++;
        if (lightingCount == C.UPDATE_LIGHTING_INTERVAL) {
            C.UPDATE_LIGHTING = true;
            lightingCount = 0;
        } else {
            C.UPDATE_LIGHTING = false;
        }

        shapeRenderer.setProjectionMatrix(cam.combined);
        spriteBatch.setProjectionMatrix(cam.combined);

        //lists
        this.updateLists();
        this.clearDumps();

        world.step(Gdx.graphics.getDeltaTime(), 3, 4);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        cam.update();
//		renderer.draw(view.getWorld());
        Gdx.gl.glFlush();
        handleKeys();

        player.update(frame);
        player.draw(spriteBatch, shapeRenderer);

        DebugText.addMessage("activezombies", "Active Zombies: " + activeZombies.size());

        for (Zombie z: (ArrayList<Zombie>)activeZombies.clone()) {
            z.update(frame);
            z.draw(spriteBatch, shapeRenderer);
        }

        HUDSpriteBatch.begin();
        HUDSpriteBatch.enableBlending();
        hud.render(HUDSpriteBatch);
        HUDSpriteBatch.end();

        for (DebugDots dd: debugDots) {
            dd.draw(spriteBatch, shapeRenderer);
        }
        DebugText.render();
    }

    protected void updateLoop() {
        mh.update();
        hud.update();

        frame++;
        if (frame > 2000)
            frame = 0;
    }

    protected void updateLists() {
        for (DyingZombie z: dyingZombie) {
            z.update();
            z.draw();
        }
    }

    protected void handleKeys() {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            handleKeysDesktop();
        } else {
            handleKeysAndroid();
        }
    }

    protected void handleContacts() {
        for (Contact c: world.getContactList()) {
            Fixture f1 = c.getFixtureA();
            Fixture f2 = c.getFixtureB();
            if (f1 == null || f2 == null)
                return;
            if (f1.getBody() == null || f2.getBody() == null)
                return;
            if ((BodData)f1.getBody().getUserData() == null || (BodData)f2.getBody().getUserData() == null)
                return;
            Collideable c1 = (Collideable)((BodData)f1.getBody().getUserData()).getObject();
            Collideable c2 = (Collideable)((BodData)f2.getBody().getUserData()).getObject();
            c1.handleCollision(f2);
            c2.handleCollision(f1);
        }
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resize(int arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    private void handleKeysDesktop() {
        float strength = 150;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.getBody().applyForce(new Vector2(0, strength), new Vector2(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.getBody().applyForce(new Vector2(0, -strength), new Vector2(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.getBody().applyForce(new Vector2(-strength, 0), new Vector2(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            player.getBody().applyForce(new Vector2(strength, 0), new Vector2(), true);
        }

        for (int i=0; i<3; i++) {
            if (Gdx.input.isTouched(i)) {
                int x = Gdx.input.getX(i);
                int y = Gdx.input.getY(i);
                int px = getWidth() / 2;
                int py = getHeight() / 2;
                Vector2 v = new Vector2(x - px, py - y);
                player.shoot(v);
                //view.getHUD().touch(Gdx.input.getX(i), Gdx.input.getY(i), i);
            }
        }

    }

    private void handleKeysAndroid() {
        if (C.ENABLE_ACCEL) {
            player.setMove(-Gdx.input.getPitch(), Gdx.input.getRoll());
        }
    }

    public PerspectiveCamera getCamera() {
        return cam;
    }
    public void setCamera(PerspectiveCamera cam){
        this.cam = cam;
    }
    public ShapeRenderer getShapeRenderer() {return shapeRenderer;}
    public SpriteBatch getHUDSpriteBatch() {return HUDSpriteBatch;}
    public SpriteBatch getSpriteBatch() {return spriteBatch;}
    public void addActiveZombie(Zombie z) {
        if (activeZombies.indexOf(z) == -1)
            activeZombies.add(z);
    }
    public boolean removeActiveZombie(Zombie z) {
        return activeZombies.remove(z);
    }
    public ArrayList<Zombie> getActiveZombies() {return activeZombies;}

    public void addDebugDots(Vector2 p1, Vector2 p2) {
        debugDots.add(new DebugDots(this, p1, p2));
    }
    public void clearDebugDots() {
        debugDots = new LinkedList<DebugDots>();
    }
}