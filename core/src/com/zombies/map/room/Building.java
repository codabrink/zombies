package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.interfaces.HasZone;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Building implements HasZone {
    public static final int[] MODIFIERS = {1, 0, 0, 1, -1, 0, 0, -1};

    public enum DataState { BAD, PROCESSING, GOOD }

    public static HashSet<Room> unFinalizedRooms = new HashSet<>();

    public DataState wallMapState = DataState.BAD;

    public boolean threadLocked = false;
    private HashSet<Room> rooms = new HashSet<>();
    public HashMap<String, Box> boxMap = new HashMap<>();
    public HashMap<String, Wall> wallMap = new HashMap<>();
    private Vector2 center;
    private Zone zone;

    public Building(Vector2 center) {
        this.center = center;
        Zone.getZone(center).addObject(this);
    }

    public void update() {
        for (Room r : unFinalizedRooms)
            r.finalize();
    }

    public void refresh(Room room) { // room is the room that caused the refresh
        wallMapState = DataState.BAD;
        for (Box b : boxMap.values())
            rooms.add(b.getRoom());
    }

    public Vector2 positionOf(int[] key) {
        float vx = center.x - C.BOX_RADIUS + (C.BOX_DIAMETER * key[0]);
        float vy = center.y - C.BOX_RADIUS + (C.BOX_DIAMETER * key[1]);
        return new Vector2(vx, vy);
    }
    public void associateBoxes() {
        for (Box b : boxMap.values()) {
            int[][] adjBMKeys = getAdjBMKeys(b.getKey());
            for (int i = 0; i < adjBMKeys.length; i++) {
                int[] key = adjBMKeys[i];
                Box bb = boxMap.get(key);
                //if (bb != null)
                    //b.setAdjBox(C.DIRECTIONS[i / 2], bb);
            }
        }
    }

    public void putBoxMap(int[] key, Box b) {
        boxMap.put(key[0] + "," + key[1], b);
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

    public Vector2 getCenter() {
        return center;
    }
    public HashSet<Room> getRooms() { return rooms; }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        zone = z;
    }
}