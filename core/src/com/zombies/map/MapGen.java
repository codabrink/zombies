package com.zombies.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Room;
import com.zombies.Zone;
import com.zombies.interfaces.Overlappable;

import java.util.HashMap;
import java.util.Random;

public class MapGen {

    public static int[] DIRECTIONS = {0, 90, 180, 270};

    public static void update(Zone z) {
        // needs to be done during generation, not creation
        if (z.getAdjZones().size() < 8)
            z.findAdjZones();

        // can the zone generate a room?
        if (z.getRooms().size() < z.numRooms && z.roomGenFailureCount < z.numRooms * 2) {
            Room room = genRoom(z);
            if (room != null)
                z.addObject(room);
            else
                z.roomGenFailureCount++;
        }
    }

    public static void fillZone(Zone z) {
        Random r = new Random();

        // needs to be done during generation, not creation
        if (z.getAdjZones().size() < 8)
            z.findAdjZones();

        for (int i=0;i<=0;i++) {
            Room room = genRoom(z);
            if (room != null)
                z.addObject(room);
            else
                break;
        }
    }

    public static Hallway genHallway(Room r) {
        Random rnd = GameView.gv.random;
        Box b = r.getOuterBoxes().get(rnd.nextInt(r.getOuterBoxes().size()));
        return genHallway(b);
    }

    public static Hallway genHallway(Box b) {
        return new Hallway(b, b.getRandomOpenDirection(), 4);
    }

    private static Room genRoom(Zone z) {
        Random r = new Random();
        HashMap<String, Box> boxMap = new HashMap<>();
        for (int i=0; i <= 5; i++) { // try 5 times
            Vector2 boxPosition = z.randomDiscretePosition(20);
            if (collidesWith(z, boxPosition, C.BOX_SIZE, C.BOX_SIZE) == null) {
                Box b = new Box(boxPosition.x, boxPosition.y);
                b.BMKey = "0,0";
                boxMap.put("0,0", b);
                break;
            }
        }

        // if it failed to find an open position
        if (boxMap.size() == 0)
            return null;

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (boxMap.size() <= roomSize) {
            Object[] boxMapArray = boxMap.values().toArray(); // so we can grab a random box

            // find a box with at least one open side
            Box b;
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
            if (collidesWith(z, proposedPosition, C.BOX_SIZE, C.BOX_SIZE) == null) {
                Box bb = new Box(proposedPosition.x, proposedPosition.y);
                bb.BMKey = newBMLocation[0] + "," + newBMLocation[1];
                boxMap.put(bb.BMKey, bb);
                associate(bb, boxMap);
            }

            loops++;
            if (loops > roomSize * 4) // catch infinite loops
                break;
        }

        Room room = new Room(boxMap.values());
        // genHallway(room);
        return room;
    }

    private static void associate(Box b, HashMap<String, Box> boxMap) {
        int[] BMLocation = b.getBMLocation();
        int[] modifiers = {0, 1, 1, 0, 0, -1, -1, 0};
        for (int i = 0; i <= modifiers.length - 1; i = i + 2) {
            Box bb = boxMap.get((BMLocation[0]+modifiers[i])+","+(BMLocation[1]+modifiers[i+1]));
            if (bb == null)
                continue;

            b.setAdjBox(DIRECTIONS[i/2], bb);
            bb.setAdjBox(DIRECTIONS[(i/2 + 2) % 4], b);
        }
    }

    public static Overlappable collidesWith(Zone z, Vector2 p, float w, float h) {
        for (Zone zone : z.getAdjZonesPlusSelf()) {
            for (Overlappable o : zone.getOverlappables()) {
                if (o.overlaps(p.x, p.y, w, h))
                    return o;
            }
        }
        return null;
    }

    private static boolean valueInRange(float value, float min, float max) {
        return (value > min) && (value < max);
    }
}
