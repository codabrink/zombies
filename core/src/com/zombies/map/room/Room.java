package com.zombies.map.room;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.zombies.C;
import com.zombies.Unit;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Updateable;
import com.zombies.util.Assets.MATERIAL;
import com.zombies.util.U;

public class Room implements Loadable, HasZone, Updateable {
    public enum RoomType {
        LIVING_ROOM (MATERIAL.FLOOR_CARPET),
        DINING_ROOM (MATERIAL.FLOOR_WOOD),
        KITCHEN (MATERIAL.GREEN_TILE);

        public MATERIAL floorMaterial;
        RoomType(MATERIAL floorMaterial) {
            this.floorMaterial = floorMaterial;
        }

        public static RoomType random() {
            return values()[random.nextInt(values().length)];
        }
    }

    public static int roomCount = 0;
    private static Random random = new Random();

    public RoomType roomType;
    private int id;
    public  HashSet<Box> boxes = new HashSet<>();
    public boolean connected = false;
    private boolean alarmed = false;
    private Zone zone;
    private Vector2 center;

    private Building building;
    public HashSet<String> doors = new HashSet<>();

    public static Room createRoom(Building building, int[] key, int maxBoxes) {
        if (building.checkOverlap(key) != null)
            return null;
        return new Room(building, key, maxBoxes);
    }

    protected Room(Building building, int[] key, int maxBoxes) {
        roomType = RoomType.random();
        generate(key, maxBoxes);

        building.addRoom(this);
        this.building = building;

        id = roomCount;
        roomCount++;

        D.update();
    }

    private void generate(int[] key, int maxSize) {
        new Box(this, key);

        int loops = 0; Box b;
        float cx = 0, cy = 0;
        while (boxes.size() < maxSize) {
            loops++;
            if (loops > maxSize * C.ERROR_TOLERANCE)
                break;

            b = (Box)U.random(getOuterBoxes());
            if (b == null)
                continue;

            key = (int[]) U.random(b.getOpenAdjKeys());
            b = Box.createBox(this, key);

            cx += b.getCenter().x;
            cy += b.getCenter().y;
        }

        center = new Vector2(cx / boxes.size(), cy / boxes.size());
    }

    public void compile() {
        zone = Zone.getZone(center);
        zone.addPendingObject(this);

        for (Box b: boxes)
            b.setAdjWallMap();
    }

    public void load() {
        for (Box b : boxes)
            b.load();
    }

    public void unload() {
        for (Box b: boxes)
            b.unload();
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