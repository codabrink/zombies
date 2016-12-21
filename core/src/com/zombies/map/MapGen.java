package com.zombies.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.data.Data;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;
import com.zombies.map.thread.RunnableRoomGen;
import com.zombies.util.Geometry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class MapGen {
    public static final int[] DIRECTIONS = {0, 90, 180, 270};

    public static RunnableRoomGen runnableRoomGen = new RunnableRoomGen();
    public static Thread roomGen = new Thread(runnableRoomGen);

    private static long lastGenTimestamp;

    public static void update(Zone z) {
        if (Data.currentRoom != null && Data.currentRoom.joinOverlappableOverlappables.size() < 3) {

        }
        if (!roomGen.isAlive()) {

            //roomGen.start();
        }
    }

    public static Room initialRoom() {
        Room initialRoom = genRoom(new Vector2(-C.BOX_SIZE / 2, -C.BOX_SIZE / 2));
        Zone.getZone(0, 0).addObject(initialRoom);
        lastGenTimestamp = System.currentTimeMillis();
        return initialRoom;
    }

    public static Hallway genHallway(Box b) {
        return new Hallway(b, b.getRandomOpenDirection(), 4);
    }

    public static void connectRoom(Room r) {
        for (Box b : r.getOuterBoxes()) {
            for (int i : DIRECTIONS) {
                if (b.getAdjBox(i) == null) {
                    double rad = Math.toRadians(i);
                    Vector2 p = Geometry.projectVector(b.getCenter(), rad, C.BOX_SIZE);
                    Box bb;
                    Zone zone = Zone.getZone(p);
                    HashSet<Zone> zones = zone.getAdjZones(1);

                    for (Zone z : zones) {
                        bb = z.getBox(p);

                        if (bb != null && bb.getRoom() != b.getRoom())
                            connectBoxes(b, bb);
                    }
                }
            }
        }
    }

    public static void connectBoxes(Box b, Box bb) {
        float dx = Math.abs(bb.getCenter().x - b.getCenter().x);
        float dy = Math.abs(bb.getCenter().y - b.getCenter().y);
        double theta = Math.toDegrees(Math.atan2(dy, dx));

        theta = Math.round(theta / 90) * 90;
        new Hallway(b, (int)theta, C.HALLWAY_WIDTH);
    }

    public static Room genRoom(Zone z) {
        for (int i = 0; i <= 5; i++) { // try 5 times
            Vector2 boxPosition = z.randomPosition();
            if (z.checkOverlap(boxPosition, C.BOX_SIZE, C.BOX_SIZE, 1) == null) {
                return genRoom(boxPosition);
            }
        }
        return null;
    }

    public static Room genRoom(Vector2 boxPosition) {
        Zone z = Zone.getZone(boxPosition);
        Random r = new Random();
        HashMap<String, Box> boxMap = new HashMap<>();

        Box b = new Box(boxPosition.x, boxPosition.y);
        b.BMKey = "0,0";
        boxMap.put("0,0", b);

        // if it failed to find an open position
        if (boxMap.size() == 0)
            return null;

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (boxMap.size() <= roomSize) {
            Object[] boxMapArray = boxMap.values().toArray(); // so we can grab a random box

            // find a box with at least one open side
            do {
                b = (Box)boxMapArray[r.nextInt(boxMapArray.length)];
            } while (b.getAdjBoxes().size() == 4);

            // find open side (this can be improved)
            int direction;
            do {
                direction = DIRECTIONS[r.nextInt(4)];
            } while (b.getAdjBox(direction) != null);

            int[] newBMLocation = b.getBMLocation();

            // rasterize that direction
            Vector2 proposedPosition = new Vector2();
            switch (direction) {
                case 0: // right
                    proposedPosition = b.getPosition().cpy().add(b.width, 0);
                    newBMLocation[0]++;
                    break;
                case 90: // top
                    proposedPosition = b.getPosition().cpy().add(0, b.height);
                    newBMLocation[1]++;
                    break;
                case 180: // left
                    proposedPosition = b.getPosition().cpy().sub(b.width, 0);
                    newBMLocation[0]--;
                    break;
                case 270: // bottom
                    proposedPosition = b.getPosition().cpy().sub(0, b.height);
                    newBMLocation[1]--;
                    break;
            }

            if (z.checkOverlap(proposedPosition, C.BOX_SIZE, C.BOX_SIZE, 1) == null) {
                Box bb = new Box(proposedPosition.x, proposedPosition.y);
                bb.BMKey = newBMLocation[0] + "," + newBMLocation[1];
                boxMap.put(bb.BMKey, bb);
                associate(bb, boxMap);
            }

            loops++;
            if (loops > roomSize * 4) // catch infinite loops
                break;
        }

        Room room = new Room(boxMap);
        // genHallway(room);
        return room;
    }

    public static void associate(Box b, HashMap<String, Box> boxMap) {
        int[] BMLocation = b.getBMLocation();
        int[] modifiers = {1, 0, 0, 1, -1, 0, 0, -1};
        for (int i = 0; i <= modifiers.length - 1; i += 2) {
            Box bb = boxMap.get((BMLocation[0]+modifiers[i])+","+(BMLocation[1]+modifiers[i+1]));
            if (bb == null)
                continue;

            b.setAdjBox(DIRECTIONS[i/2], bb);
            bb.setAdjBox(DIRECTIONS[(i/2 + 2) % 4], b);
        }
    }
}
