package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.map.MapGen;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Generator {
    public static Room genRoomAndZone(Vector2 startPos) { return zoneRoom(genRoom(startPos)); }
    public static Room genRoom(Vector2 startPos) {
        // startPos is the bottom left corner of the first box
        Zone z = Zone.getZone(startPos);
        Random r = new Random();
        HashMap<String, Box> boxMap = new HashMap<>();

        Box b = new Box(startPos.x, startPos.y);
        b.BMKey = "0,0";
        boxMap.put("0,0", b);

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (boxMap.size() <= roomSize) {
            Object[] boxMapArray = boxMap.values().toArray(); // so we can grab a random box

            // find a box with at least one open side
            do {
                b = (Box) boxMapArray[r.nextInt(boxMapArray.length)];
            } while (b.getAdjBoxes().size() == 4);

            // find open side (this can be improved)
            int direction;
            do {
                direction = MapGen.DIRECTIONS[r.nextInt(4)];
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
                MapGen.associate(bb, boxMap);
            }

            loops++;
            if (loops > roomSize * 4) // catch infinite loops
                break;
        }

        return new Room(boxMap);
    }
    public static Room zoneRoom(Room r) {
        HashSet<Zone> zones = new HashSet<>();
        for (Box b : r.getBoxes())
            zones.add(Zone.getZone(b.getPosition()).addObject(b));
        for (Zone z : zones)
            z.addObject(r);
        return r;
    }
}
