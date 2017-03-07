package com.zombies.map.room;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.interfaces.Gridable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Modelable;
import com.zombies.map.Hallway;
import com.zombies.util.Assets;

import java.util.HashMap;
import java.util.HashSet;

public class Building implements HasZone, Modelable {
    public static final int[] MODIFIERS = {1, 0, 0, 1, -1, 0, 0, -1};

    private Model wallModel, floorModel;
    private ModelInstance wallModelInstance, floorModelInstance;

    private int drawFrame = 0;
    public int xLow = 0, xHigh = 0, yLow = 0, yHigh = 0;
    public boolean threadLocked = false;
    private HashSet<Room> rooms = new HashSet<>();
    public HashMap<String, Gridable> gridMap = new HashMap<>();
    public HashMap<String, Wall> wallMap = new HashMap<>();
    public HashSet<Hallway> hallways = new HashSet<>();
    private Vector2 center;
    private Zone zone;

    private boolean compiled = false; // debug var

    public Building(Vector2 center) {
        this.center = center;

        Zone z = Zone.getZone(center);
        synchronized (z.pendingObjects) {
            z.pendingObjects.add(this);
        }
    }
    public void compile() {
        for (Room room : rooms)
            room.compile();
        for (Hallway hallway : hallways)
            hallway.compile();
        calculateBorders();
        if (C.DEBUG) compiled = true;
    }

    public Vector2 positionOf(int[] key) {
        return positionOf(key[0], key[1]);
    }
    public Vector2 positionOf(int x, int y) {
        float vx = center.x - C.GRID_HALF_SIZE + (C.GRID_SIZE * x);
        float vy = center.y - C.GRID_HALF_SIZE + (C.GRID_SIZE * y);
        return new Vector2(vx, vy);
    }

    public Vector2[] cornersOf(int[] key) { return cornersOf(key[0], key[1]); }
    public Vector2[] cornersOf(int x, int y) { return cornersOf(positionOf(x, y)); }
    public Vector2[] cornersOf(Vector2 position) {
        return new Vector2[] {
                new Vector2(position.x + C.GRID_SIZE, position.y + C.GRID_SIZE),
                new Vector2(position.x, position.y + C.GRID_SIZE),
                new Vector2(position.x, position.y),
                new Vector2(position.x + C.GRID_SIZE, position.y)
        };
    }

    public HashSet<Box> boxesOnCol(int col) {
        HashSet<Box> boxes = new HashSet<>();
        Gridable g;
        for (int y = yLow; y <= yHigh; y++) {
            g = gridMap.get(col + "," + y);
            if (g instanceof Box)
                boxes.add((Box)g);
        }
        return boxes;
    }

    public HashSet<Box> boxesOnRow(int row) {
        HashSet<Box> boxes = new HashSet<>();
        Gridable g;
        for (int x = xLow; x <= xHigh; x++) {
            g = gridMap.get(x + "," + row);
            if (g instanceof Box)
                boxes.add((Box)g);
        }
        return boxes;
    }

    public Overlappable checkOverlap(int[] key) {
        return checkOverlap(key, C.GRID_SIZE, C.GRID_SIZE);
    }
    public Overlappable checkOverlap(int[] key, float width, float height) {
        Vector2 position = positionOf(key);
        Zone    zone     = Zone.getZone(position);
        return zone.checkOverlap(position, width, height, 1);
    }

    public Vector2[] wallPositionOf(String key) {
        String[] parts = key.split(",");
        return wallPositionOf(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2].charAt(0));
    }
    public Vector2[] wallPositionOf(int x, int y, char orientation) {
        Vector2 p1 = positionOf(new int[]{x, y});
        return new Vector2[]{
                p1,
                (orientation == 'v' ? p1.cpy().add(0, C.GRID_SIZE) : p1.cpy().add(C.GRID_SIZE, 0))};
    }

    public static String wallBetweenGridables(int[] k1, int[] k2) {
        return Math.max(k1[0], k2[0]) + "," +
                Math.max(k1[1], k2[1]) + "," +
                (k1[0] != k2[0] ? "v" : "h");
    }
    public static String wallKeyBetweenGridables(Gridable g1, Gridable g2) {
        return wallBetweenGridables(g1.getKey(), g2.getKey());
    }
    public Wall wallBetweenBoxes(Box b1, Box b2) {
        return wallMap.get(wallKeyBetweenGridables(b1, b2));
    }

    public HashSet<Box> getOuterBoxes() {
        HashSet<Box> boxes = new HashSet<>();
        for (Gridable g : gridMap.values())
            if (g instanceof Box && ((Box)g).getOpenAdjKeys().size() > 0)
                boxes.add((Box)g);
        return boxes;
    }
    public Gridable gridMapGet(int[] key) {
        return gridMap.get(key[0] + "," + key[1]);
    }
    public Gridable gridMapPut(int[] key, Gridable g) {
        Gridable oldGridable = gridMapGet(key);
        gridMap.put(key[0] + "," + key[1], g);
        return oldGridable;
    }

    public void putBoxMap(int[] key, Box b) {
        gridMap.put(key[0] + "," + key[1], b);
    }
    public void putWallMap(String key, Wall w) {
        if (wallMap.get(key) != null)
            wallMap.get(key).destroy();
        wallMap.put(key, w);
    }

    public static int[] directionToBMKey(int[] bmKey, int direction) {
        int[] key = bmKey.clone();
        switch(direction) {
            case 0:
                key[0]++;
                break;
            case 1:
                key[1]++;
                break;
            case 2:
                key[0]--;
                break;
            case 3:
                key[1]--;
                break;
        }
        return key;
    }
    public static int[][] getAdjBMKeys(int[] key) {
        int[][] adjKeys = new int[4][];
        for (int i = 0; i < MODIFIERS.length; i += 2) {
            adjKeys[i / 2] = new int[] { key[0] + MODIFIERS[i], key[1] + MODIFIERS[i + 1] };
        }
        return adjKeys;
    }

    public void calculateBorders() {
        int[] key;
        for (Gridable g : gridMap.values()) {
            if (!(g instanceof Box)) continue;

            key = ((Box)g).getKey();
            xLow  = Math.min(xLow,  key[0]);
            xHigh = Math.max(xHigh, key[0]);
            yLow  = Math.min(yLow,  key[1]);
            yHigh = Math.max(yHigh, key[1]);
        }
    }
    public Vector2 getCenter() {
        return center;
    }
    public void addRoom(Room room) {
        if (C.DEBUG) { compiled = false; }
        rooms.add(room);
    }
    public HashSet<Room> getRooms() { return rooms; }
    public HashSet<Hallway> getHallways() { return hallways; }

    @Override
    public void rebuildModel() {
        if (C.DEBUG && !compiled)
            System.out.println("Building: ERROR! Building is not compiled.");
        buildFloorMesh();
        buildWallMesh();
    }
    public void buildWallMesh() {
        for (Wall w : wallMap.values())
            w.genSegmentsFromPoints();

        Assets.modelBuilder.begin();
        MeshPartBuilder wallBuilder = Assets.modelBuilder.part("Walls",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        for (Wall w : wallMap.values())
            w.buildWallMesh(wallBuilder, center);
        for (Gridable g : gridMap.values())
            g.buildWallMesh(wallBuilder, center);
        MeshPartBuilder frameBuilder = Assets.modelBuilder.part("DoorFrames",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)));
        //for (DoorContainer dc : doorContainers)
        //    dc.getDoorFrame().buildMesh(frameBuilder, center);

        wallModel = Assets.modelBuilder.end();
        wallModelInstance = new ModelInstance(wallModel);
        wallModelInstance.transform.setTranslation(center.x, center.y, 0);
    }
    public void buildFloorMesh() {
        Assets.modelBuilder.begin();
        MeshPartBuilder floorBuilder = Assets.modelBuilder.part("floor",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(Assets.floor1Diffuse));
        for (Gridable g: gridMap.values()) {
            g.buildFloorMesh(floorBuilder, center);
        }
        floorModel = Assets.modelBuilder.end();
        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setTranslation(center.x, center.y, 1);
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        if (drawFrame == GameView.gv.frame)
            return;
        drawFrame = GameView.gv.frame;

        modelBatch.begin(GameView.gv.getCamera());
        if (floorModelInstance != null)
            modelBatch.render(floorModelInstance, GameView.environment);
        if (wallModelInstance != null)
            modelBatch.render(wallModelInstance, GameView.environment);
        modelBatch.end();
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        zone = z;
    }
}