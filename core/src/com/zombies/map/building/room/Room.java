package com.zombies.map.building.room;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.zombies.C;
import com.zombies.Unit;
import com.zombies.Zone;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Updateable;
import com.zombies.map.building.Box;
import com.zombies.map.building.Building;
import com.zombies.util.Assets.MATERIAL;
import com.zombies.util.U;

public class Room implements Loadable, HasZone, Updateable {
    public enum RoomType {
        LIVING_ROOM (MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.FLOOR_CARPET, Room.class),
        DINING_ROOM (MATERIAL.WALL_PAINTED_RED, MATERIAL.FLOOR_WOOD, Room.class),
        KITCHEN (MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.GREEN_TILE, Kitchen.class);

        public MATERIAL floorMaterial;
        public MATERIAL wallMaterial;
        public Class    klass;
        RoomType(MATERIAL wallMaterial, MATERIAL floorMaterial, Class klass) {
            this.wallMaterial  = wallMaterial;
            this.floorMaterial = floorMaterial;
            this.klass         = klass;
        }

        public static RoomType random() {
            return values()[random.nextInt(values().length)];
        }
    }

    public static int roomCount = 0;
    private static Random random = new Random();

    public RoomType type;
    private int id;
    public  HashSet<Box> boxes = new HashSet<>();
    public  HashSet<Room> connections = new HashSet<>();
    public boolean connected = false;
    private boolean alarmed = false;
    private Zone zone;
    private Vector2 center;

    protected HashMap<Class, HashSet<Object>> objectMap = new HashMap<>();

    private Building building;

    public static Room createRoom(Building building, int[] key, int maxBoxes) {
        if (building.checkOverlap(key) != null)
            return null;

        RoomType type = RoomType.random();
        switch (type) {
            case KITCHEN:
                return new Kitchen(building, key, maxBoxes, type);
            default:
                return new Room(building, key, maxBoxes, type);
        }
    }

    protected Room(Building building, int[] key, int maxBoxes, RoomType roomType) {
        type = roomType;

        building.addRoom(this);
        this.building = building;

        generate(key, maxBoxes);

        id = roomCount;
        roomCount++;
    }

    protected void generate(int[] key, int maxBoxes) {
        if (maxBoxes == 0)
            return;

        Box.createBox(this, key);

        int loops = 0; Box b;
        float cx = 0, cy = 0;
        while (boxes.size() < maxBoxes) {
            loops++;
            if (loops > maxBoxes * C.ERROR_TOLERANCE)
                break;

            b = (Box)U.random(getOuterBoxes());
            if (b == null)
                break;

            key = (int[]) U.random(b.getOpenAdjKeys());
            b = Box.createBox(this, key);

            if (b == null)
                continue;

            cx += b.getCenter().x;
            cy += b.getCenter().y;
        }

        center = new Vector2(cx / boxes.size(), cy / boxes.size());
    }

    public void compile() {
        for (Box b: boxes)
            b.compile();

        if (center != null)
            return;

        float cx = 0, cy = 0;
        for (Box b : boxes) {
            cx += b.getCenter().x;
            cy += b.getCenter().y;
        }
        center = new Vector2(cx / boxes.size(), cy / boxes.size());
        zone = Zone.getZone(center);
        zone.addPendingObject(this);

    }

    public void load() {
    }

    public void unload() {
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

    public void buildFloorMesh(MeshPartBuilder builder, Vector2 center) {
        for (Box b : boxes)
            b.buildFloorMesh(builder, center);
    }

    public String giveKey(Room r) {
        return Math.min(id, r.getId()) + "," + Math.max(id, r.getId());
    }

    @Override
    public void update() {
    }
}