package com.zombies;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.GL20;
import com.zombies.HUD.HUD;
import com.zombies.data.Data;
import com.zombies.data.Stats;
import com.zombies.map.room.*;
import com.zombies.map.room.Building;
import com.zombies.map.thread.Generator;
import com.zombies.util.Assets;
import com.zombies.interfaces.Collideable;
import com.zombies.workers.BuildingWallMapWorker;
import com.zombies.workers.RoomDoorWorker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class GameView implements Screen {
    // STATIC VARIABLES
    public static com.zombies.HUD.FontGen fontGen;
    public static GameView gv;
    public static Environment environment, outsideEnvironment;
    public static Player player;
    public static Random r = new Random();

    public Stats stats;

    private SpriteBatch HUDSpriteBatch;
    public SpriteBatch spriteBatch;
    public ShapeRenderer shapeRenderer;
    public ModelBatch modelBatch;
    private Assets assets = new Assets();

    protected World world;

    public Random random = new Random();
    protected CameraHandle camHandle;
    protected Meshes meshes = new Meshes();
    protected int lightingCount = 0;
    protected ThumbpadLeft thumbpadLeft;
    protected ThumbpadRight thumbpadRight;
    public MessageHandler mh;
    private com.zombies.HUD.HUD hud;
    private LinkedList<DebugDots> debugDots = new LinkedList<DebugDots>();
    private ArrayList<DebugCircle> debugCircles = new ArrayList<DebugCircle>();
    public int frame = 0;

    private Box2DDebugRenderer debugRenderer;
    private Matrix4 debugMatrix;

    //lists
    LinkedList<PostponedZombie> postZombie = new LinkedList<PostponedZombie>();
    LinkedList<PostponedZombie> postZombieDump = new LinkedList<PostponedZombie>();
    LinkedList<DyingZombie> dyingZombie = new LinkedList<DyingZombie>();
    LinkedList<DyingZombie> dyingZombieDump = new LinkedList<DyingZombie>();

    public GameView() {
        shapeRenderer = new ShapeRenderer();
        HUDSpriteBatch = new SpriteBatch();
        spriteBatch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();

        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Data.workers = new HashMap<>();
        //Data.workers.put("BuildingWallMap", new Thread(new BuildingWallMapWorker()));
        //Data.workers.get("BuildingWallMap").start();
        Data.workers.put("RoomDoor", new Thread(new RoomDoorWorker()));
        Data.workers.get("RoomDoor").start();
    }

    public void reset() {
        gv = this;

        Zone.zones = new HashMap<>();
        Zone.readyToModel = new HashSet<>();
        Zone.loadedZones = new ArrayList<>();
        stats = new Stats();

        hud = new com.zombies.HUD.HUD();
        world = new World(new Vector2(), true);

        player = new Player(new Vector2(0, 0));

        camHandle = new CameraHandle(this);
        thumbpadLeft = new ThumbpadLeft(this);
        thumbpadRight = new ThumbpadRight(this);
        mh = new MessageHandler(this);
        //meshes.main.play();

        Gdx.input.setInputProcessor(hud);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(player.pointLight);

        outsideEnvironment = new Environment();
        outsideEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));

        // worker resetting
        RoomDoorWorker.roomList = new LinkedList<>();

        System.gc();
    }

    public void initialRoom() {
        Generator.genRoom(new Building(new Vector2(0, 0)), new int[]{0, 0});
    }

    public HUD getHUD() {
        return hud;
    }

    public void addMessage(Message m) {
        mh.addMessage(m);
    }

    public Game getGame() {
        return Zombies.instance;
    }

    public ThumbpadLeft getThumbpadLeft() {
        return thumbpadLeft;
    }
    public ThumbpadRight getThumbpadRight() { return thumbpadRight;}

    public Meshes getMeshes() {
        return meshes;
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
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

        shapeRenderer.setProjectionMatrix(camHandle.cam.combined);
        spriteBatch.setProjectionMatrix(camHandle.cam.combined);

        //lists
        world.step(Gdx.graphics.getDeltaTime(), 3, 4);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        // renderer.draw(view.getWorld());
        Gdx.gl.glFlush();
        handleKeys();

        player.update(frame);

        for (DebugCircle dc: debugCircles)
            dc.draw(spriteBatch, shapeRenderer, modelBatch);
        for (DebugDots dd: debugDots)
            dd.draw(spriteBatch, shapeRenderer, modelBatch);
        player.draw(spriteBatch, shapeRenderer, modelBatch);

        HUDSpriteBatch.begin();
        HUDSpriteBatch.enableBlending();
        hud.render(HUDSpriteBatch, shapeRenderer, modelBatch);
        HUDSpriteBatch.end();

        com.zombies.HUD.DebugText.render();
        //debugRenderer.render(world, cam.combined);
    }

    protected void updateLoop() {
        mh.update();
        hud.update();

        frame++;
        if (frame > 2000)
            frame = 0;
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
    public void dispose() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resize(int width, int height) {
        camHandle.resize(width, height);
    }

    @Override
    public void resume() {}

    @Override
    public void show() {
        reset();
        initialRoom();
    }

    private void handleKeysDesktop() {
        float strength = 50 * C.SCALE;

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.getBody().setTransform(player.getBody().getPosition().add(0, C.BOX_DIAMETER), player.getBody().getAngle());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            player.getBody().setTransform(player.getBody().getPosition().add(C.BOX_DIAMETER, 0), player.getBody().getAngle());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            player.getBody().setTransform(player.getBody().getPosition().add(0, -C.BOX_DIAMETER), player.getBody().getAngle());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            player.getBody().setTransform(player.getBody().getPosition().add(-C.BOX_DIAMETER, 0), player.getBody().getAngle());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Zone.createHole(player.getPosition().cpy(), 4.0f);
            debugCircles.add(new DebugCircle(player.getPosition().cpy(), 4.0f));
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

    public void end() {
        Zombies.instance.setScreen(new EndView(stats));
    }

    private void handleKeysAndroid() {
        if (C.ENABLE_ACCEL) {
            player.setMove(-Gdx.input.getPitch(), Gdx.input.getRoll());
        }
    }

    public PerspectiveCamera getCamera() {
        return camHandle.cam;
    }
    public ShapeRenderer getShapeRenderer() {return shapeRenderer;}
    public SpriteBatch getHUDSpriteBatch() {return HUDSpriteBatch;}
    public SpriteBatch getSpriteBatch() {return spriteBatch;}

    public void addDebugDots(Vector2 p1, Vector2 p2) {
        debugDots.add(new DebugDots(p1));
        debugDots.add(new DebugDots(p2));
    }
    public void addDebugDots(Vector2 p1, Color c) { debugDots.add(new DebugDots(p1, c)); }
    public void clearDebugDots() {
        debugDots = new LinkedList<DebugDots>();
    }
}