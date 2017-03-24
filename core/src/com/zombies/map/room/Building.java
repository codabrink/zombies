package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.interfaces.Gridable;
import com.zombies.interfaces.HasZone;
import com.zombies.map.Hallway;
import com.zombies.util.U;
import com.zombies.workers.RoomDoorWorker;

import java.util.HashMap;
import java.util.HashSet;

public class Building implements HasZone {
    public static final int[] MODIFIERS = {1, 0, 0, 1, -1, 0, 0, -1};

    public int xLow = 0, xHigh = 0, yLow = 0, yHigh = 0;
    public boolean threadLocked = false;
    protected HashSet<Room> rooms = new HashSet<>();
    public HashMap<String, Gridable> gridMap = new HashMap<>();
    public HashMap<String, Wall> wallMap = new HashMap<>();
    public HashSet<Hallway> hallways = new HashSet<>();
    protected Vector2 center;
    protected Zone zone;

    public static Building createBuilding(Vector2 c, int maxRooms) {
        Zone z = Zone.getZone(c);
        float bufferRadius = C.GRIDSIZE * 5;
        float bufferDiameter = bufferRadius * 2;
        if (z.checkOverlap(new Overlappable(c, bufferDiameter, bufferDiameter), 1, null) != null)
            return null;

        return new Building(c, maxRooms);
    }

    protected Building() {}
    protected Building(Vector2 c, int maxRooms) {
        center = c;

        generate(maxRooms);
        compile();

        for (Room room : rooms)
            RoomDoorWorker.processDoorsOnRoom(room);

        zone = Zone.getZone(center);
        zone.addPendingObject(this);
    }

    private void generate(int maxRooms) {
        if (maxRooms == 0)
            return;

        final int preferredRoomSize = 8;

        int[] key = new int[]{0, 0};
        Room.createRoom(this, key, preferredRoomSize);

        int loops = 0;

        while(rooms.size() < maxRooms && loops < maxRooms * C.ERROR_TOLERANCE) {
            loops++;
            Box b = (Box) U.random(getOuterBoxes());
            key   = (int[]) U.random(b.getOpenAdjKeys());
            Room.createRoom(this, key, preferredRoomSize);
        }
    }

    public void compile() {
        for (Room room : rooms)
            room.compile();
        for (Hallway hallway : hallways)
            hallway.compile();
        calculateBorders();
    }

    public Vector2 positionOf(int[] key) {
        return positionOf(key[0], key[1]);
    }
    public Vector2 positionOf(int x, int y) {
        float vx = center.x - C.GRID_HALF_SIZE + (C.GRIDSIZE * x);
        float vy = center.y - C.GRID_HALF_SIZE + (C.GRIDSIZE * y);
        return new Vector2(vx, vy);
    }

    public Vector2[] cornersOf(int[] key) { return cornersOf(key[0], key[1]); }
    public Vector2[] cornersOf(int x, int y) { return cornersOf(positionOf(x, y)); }
    public Vector2[] cornersOf(Vector2 position) {
        return new Vector2[] {
                new Vector2(position.x + C.GRIDSIZE, position.y + C.GRIDSIZE),
                new Vector2(position.x, position.y + C.GRIDSIZE),
                new Vector2(position.x, position.y),
                new Vector2(position.x + C.GRIDSIZE, position.y)
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
        return checkOverlap(key, 0);
    }
    public Overlappable checkOverlap(int[] key, float margin) { // margin; lke CSS margin. The area around an area.
        Gridable g = gridMapGet(key);
        if (g != null)
            return (Overlappable) g;
        return checkOverlap(key, C.GRIDSIZE, C.GRIDSIZE, margin);
    }
    public Overlappable checkOverlap(int[] key, float width, float height, float margin) {
        Vector2 position = positionOf(key);
        Zone    zone     = Zone.getZone(position);
        return zone.checkOverlap(new Overlappable(position, width, height), 1, gridMap.values());
    }

    public Vector2[] wallPositionOf(String key) {
        String[] parts = key.split(",");
        return wallPositionOf(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2].charAt(0));
    }
    public Vector2[] wallPositionOf(int x, int y, char orientation) {
        Vector2 p1 = positionOf(new int[]{x, y});
        return new Vector2[]{
                p1,
                (orientation == 'v' ? p1.cpy().add(0, C.GRIDSIZE) : p1.cpy().add(C.GRIDSIZE, 0))};
    }

    public static String wallKeyBetweenGridables(int[] k1, int[] k2) {
        return Math.max(k1[0], k2[0]) + "," +
                Math.max(k1[1], k2[1]) + "," +
                (k1[0] != k2[0] ? "v" : "h");
    }
    public static String wallKeyBetweenGridables(Gridable g1, Gridable g2) {
        return wallKeyBetweenGridables(g1.getKey(), g2.getKey());
    }
    public Wall wallBetweenBoxes(Box b1, Box b2) {
        return wallMap.get(wallKeyBetweenGridables(b1, b2));
    }
    public String wallKeyFromGridableAndDirection(Gridable g, int direction) {
        return wallKeyFromGridableAndDirection(g.getKey(), direction);
    }
    public static String wallKeyFromGridableAndDirection(int[] key, int direction) {
        switch (direction) {
            case 0:
                return (key[0] + 1) + "," + key[1] + ",v";
            case 1:
                return key[0] + "," + (key[1] + 1) + ",h";
            case 2:
                return key[0] + "," + key[1] + ",v";
            case 3:
                return key[0] + "," + key[1] + ",h";
            default:
                throw new IllegalArgumentException("Direction should be between 0 and 3.");
        }
    }
    public Wall wallFromGridableAndDirection(Gridable g, int direction) {
        return wallFromGridableAndDirection(g.getKey(), direction);
    }
    public Wall wallFromGridableAndDirection(int[] key, int direction) {
        return wallMap.get(wallKeyFromGridableAndDirection(key, direction));
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
        rooms.add(room);
    }
    public HashSet<Room> getRooms() { return rooms; }
    public HashSet<Hallway> getHallways() { return hallways; }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {}

    public static int bmKeyToDirection(int[] bmKey1, int[] bmKey2) {
        if (bmKey2[0] == bmKey1[0] + 1 && bmKey2[1] == bmKey1[1])
            return 0;
        if (bmKey2[0] == bmKey1[0] && bmKey2[1] == bmKey1[1] + 1)
            return 1;
        if (bmKey2[0] == bmKey1[0] - 1 && bmKey2[1] == bmKey1[1])
            return 2;
        if (bmKey2[0] == bmKey1[0] && bmKey2[1] == bmKey1[1] - 1)
            return 3;
        throw new IllegalArgumentException("Keys are not adjacent");
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
}