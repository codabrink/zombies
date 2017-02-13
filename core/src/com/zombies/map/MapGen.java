package com.zombies.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
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


    public static void connectRoom(Room r) {
        for (Box b : r.getOuterBoxes()) {
            for (int i : DIRECTIONS) {
                if (b.getAdjBox(i) == null) {
                    double rad = Math.toRadians(i);
                    Vector2 p = Geometry.projectVector(b.getCenter(), rad, C.BOX_DIAMETER);
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
