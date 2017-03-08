package com.zombies.map.room;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.zombies.C;
import com.zombies.Unit;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Updateable;
import com.zombies.util.Assets;
import com.zombies.util.U;

public class Room implements Loadable, HasZone, Updateable, Drawable {
    public enum GenState {CREATED, BOXED, DOORED, FINALIZED}
    public enum RoomType {
        LIVING_ROOM,
        KITCHEN;

        public static RoomType random() {
            return values()[random.nextInt(values().length)];
        }
    }
    public GenState genState;

    public static int roomCount = 0;

    private RoomType roomType = RoomType.random();
    private int id;
    public  HashSet<Box> boxes = new HashSet<>();
    public boolean connected = false;
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private boolean alarmed = false;
    private Zone zone;
    private Vector2 center;
    private static Random random = new Random();

    private Building building;
    public HashSet<String> doors = new HashSet<>();

    public Room(Building building) {
        genState = GenState.CREATED;

        this.building = building;
        building.addRoom(this);

        id = roomCount;
        roomCount++;
    }

    public void compile() {
        center = calculateMedian();
        zone = Zone.getZone(center);
        synchronized (zone.pendingObjects) {
            zone.pendingObjects.add(this);
        }

        for (Box b: boxes)
            b.setAdjWallMap();

        genState = GenState.BOXED;
    }

    public void buildModel() {
        building.rebuildModel();
        this.genState = GenState.FINALIZED;
    }

    // calculates the median position of all of the boxes
    private Vector2 calculateMedian() {
        Vector2 center = new Vector2(0, 0);
        for (Box b: boxes)
            center.add(b.getCenter());
        return new Vector2(center.x / boxes.size(), center.y / boxes.size());
    }

    public void load() {
        for (Box b : boxes)
            b.load();
        for (Wall w: walls)
            w.load();
    }

    public void unload() {
        for (Box b: boxes) {
            b.unload();
        }
        for (Wall w: walls)
            w.unload();
    }

    public void alarm(Unit victim) {
        if (!alarmed) {
            for (Box b: boxes) {
                for (Unit u: b.getUnits()) {
                    if (random.nextBoolean()) {
                        u.sick(victim);
                    }
                }
            }
            alarmed = true;
        }
    }

    // consolidate the proposed walls into as few as possible.
    public ArrayList<Vector2[]> consolidateWallPositions(ArrayList<Vector2[]> proposedPositions) {

        ArrayList<Vector2[]> iteratedPositions = new ArrayList<>(proposedPositions);

        for (Vector2[] pstn1: iteratedPositions) {
            for (Vector2[] pstn2: iteratedPositions) {

                // if the first wall's end meets the other wall's start and they have the same
                // getAngle...
                if (pstn1[1].equals(pstn2[0]) && Math.abs(pstn1[1].cpy().sub(pstn1[0]).angle() - (pstn2[1].cpy().sub(pstn2[0]).angle())) < 0.0001) {
                    Vector2[] points = new Vector2[2];
                    points[0] = pstn1[0];
                    points[1] = pstn2[1];

                    proposedPositions.add(points);

                    proposedPositions.remove(pstn1);
                    proposedPositions.remove(pstn2);

                    // keep going until no more matched walls are found.
                    proposedPositions = consolidateWallPositions(proposedPositions);
                    return proposedPositions;
                }
            }
        }

        return proposedPositions;
    }

    public Unit findUnit(Body b) {
        for (Box box: boxes) {
            for (Unit u: box.getUnits()) {
                if (u.getBody() == b)
                    return u;
            }
        }
        return null;
    }

    public ArrayList<Wall> getWalls() { return walls; }
    public HashSet<Box> getBoxes() {
        return boxes;
    }
    public Building getBuilding() { return building; }

    public int getId() { return id; }
    public HashSet<Box> getOuterBoxes() {
        HashSet<Box> outerBoxes = new HashSet<>();
        // TODO: expensive
        for (Box b : boxes) {
            if (b.getOpenAdjKeys().size() > 0)
                outerBoxes.add(b);
        }
        return outerBoxes;
    }

    private void generateDoor(Map.Entry pair) {
        System.out.println((String)pair.getKey());
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        // Zone is set in the constructor
    }

    public void rebuildFloorMesh(Vector2 center) {
        MeshPartBuilder builder = Assets.modelBuilder.part("Walls-" + id,
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(Assets.roomFloorTextures.get(roomType).textureAttribute));
        for (Box b : boxes)
            b.buildFloorMesh(builder, center);
    }


    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        building.draw(spriteBatch, shapeRenderer, modelBatch);

        if (C.DEBUG) {
            BitmapFont f = Zombies.getFont("sans-reg:8:white");
            String s = "";

            spriteBatch.begin();
            for (Box b : boxes) {
                if (C.DEBUG_SHOW_BOXMAP)
                    //s = b.getBMLocation();
                if (C.DEBUG_SHOW_ADJBOXCOUNT)
                    s = b.getAdjBoxes().size() + "";
                f.draw(spriteBatch, s, b.getPosition().x + C.GRID_SIZE / 2, b.getPosition().y + C.GRID_SIZE / 2);
            }
            spriteBatch.end();
        }
    }

    public String giveKey(Room r) {
        return Math.min(id, r.getId()) + "," + Math.max(id, r.getId());
    }

    @Override
    public void update() {
    }
}