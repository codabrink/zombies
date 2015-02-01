package com.zombies;

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

import java.util.LinkedList;
import java.util.Random;

public class GameView implements Screen {

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
    private HUD hud = new HUD(this);
    private LinkedList<DebugDots> debugDots = new LinkedList<DebugDots>();

    public float scale = 1;

    private Box2DDebugRenderer debugRenderer;
    private Matrix4 debugMatrix;

    //public objects
    public C c = new C();
    public Statistics s = new Statistics();

    //lists
    LinkedList<PostponedZombie> postZombie = new LinkedList<PostponedZombie>();
    LinkedList<PostponedZombie> postZombieDump = new LinkedList<PostponedZombie>();
    LinkedList<DyingZombie> dyingZombie = new LinkedList<DyingZombie>();
    LinkedList<DyingZombie> dyingZombieDump = new LinkedList<DyingZombie>();

    public GameView(Zombies main) {
        this.main = main;

        cam = new PerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
        HUDSpriteBatch = new SpriteBatch();
        spriteBatch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();

        grid = new Box[c.GRID_WIDTH + 1][c.GRID_HEIGHT + 1];
        world = new World(new Vector2(), true);
        setReferences();
        generateLevel();
        player = new Player(this, grid[1][1]);
        addSurvivors();
        populateLevel();
        camHandle = new CameraHandle(this);
        int radius = (int)(this.main.getWidth() * c.JOY_SIZE);
        thumbpadLeft = new ThumbpadLeft(this);
        thumbpadRight = new ThumbpadRight(this);
        shootButton = new ShootButton(this);
        mh = new MessageHandler(this);
        //meshes.main.play();

        Gdx.input.setInputProcessor(hud);
    }

    private void addSurvivors() {
        if (!c.POPULATE_SURVIVORS) return;
        for (int i=1;i<=3;i++) {
            Survivor s = player.getBox().addSurvivor();
            s.wake();
            player.addSurvivor(s);
        }
    }

    public ShootButton getShootButton() {
        return shootButton;
    }

    public HUD getHUD() {
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
        for (int i=1;i<=c.GRID_WIDTH;i++){
            for (int j=1;j<=c.GRID_HEIGHT;j++){
                if (random.nextFloat() < 0.6f) {
                    for (int k=0; k<= random.nextInt(c.BOX_MAX_ZOMBIES) + 2; k++) {
                        grid[i][j].addZombie();
                        s.numZombies ++;
                    }
                }
            }
        }
    }

    protected void generateLevel() {
        for (int i=1;i<=c.GRID_HEIGHT;i++){
            for (int j=1;j<=c.GRID_WIDTH;j++){
                if (!grid[j][i].isTouched()){
                    new Room(this, grid[j][i]);
                }
            }
        }
        for (int i=1;i<=c.GRID_HEIGHT;i++){
            for (int j=1;j<=c.GRID_WIDTH;j++){
                if (!grid[j][i].isPathed()) {
                    grid[j][i].path(c.PATH_LENGTH);
                }
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
        for (int i=1;i<=c.GRID_WIDTH;i++){
            for (int j=1;j<=c.GRID_HEIGHT;j++){
                grid[i][j] = new Box(this, (i - 1) * c.BOX_WIDTH, (j - 1) * c.BOX_HEIGHT);
            }
        }
        for (int i=1;i<=c.GRID_WIDTH;i++){
            for (int j=1;j<=c.GRID_HEIGHT;j++){
                if (j > 1){
                    grid[i][j].addBorder(grid[i][j - 1]);
                }
                else {
                    grid[i][j].addBorder(null);
                }
                if (i < c.GRID_WIDTH){
                    grid[i][j].addBorder(grid[i + 1][j]);
                }
                else {
                    grid[i][j].addBorder(null);
                }
                if (j < c.GRID_HEIGHT){
                    grid[i][j].addBorder(grid[i][j + 1]);
                }
                else {
                    grid[i][j].addBorder(null);
                }
                if (i > 1){
                    grid[i][j].addBorder(grid[i - 1][j]);
                }
                else {
                    grid[i][j].addBorder(null);
                }
            }
        }
    }

    public int getWidth() {
        return main.getWidth();
    }

    public int getHeight() {
        return main.getHeight();
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
    public void render(float delta) {
        handleContacts();
        camHandle.update();

        lightingCount ++;
        if (lightingCount == c.UPDATE_LIGHTING_INTERVAL) {
            c.UPDATE_LIGHTING = true;
            lightingCount = 0;
        } else {
            c.UPDATE_LIGHTING = false;
        }

        shapeRenderer.setProjectionMatrix(cam.combined);
        spriteBatch.setProjectionMatrix(cam.combined);

        //lists
        this.updateLists();
        this.clearDumps();

        mh.update();

        world.step(Gdx.graphics.getDeltaTime(), 3, 4);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        cam.update();
//		renderer.draw(view.getWorld());
        HUDSpriteBatch.begin();

        HUDSpriteBatch.enableBlending();

        hud.update();
        hud.render(HUDSpriteBatch);

        HUDSpriteBatch.end();
        Gdx.gl.glFlush();
        handleKeys();
        player.draw(spriteBatch, shapeRenderer);

        for (DebugDots dd: debugDots) {
            dd.draw(spriteBatch, shapeRenderer);
        }
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
        if (c.ENABLE_ACCEL) {
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

    public void addDebugDots(Vector2 p1, Vector2 p2) {
        debugDots.add(new DebugDots(this, p1, p2));
    }
    public void clearDebugDots() {
        debugDots = new LinkedList<DebugDots>();
    }
}