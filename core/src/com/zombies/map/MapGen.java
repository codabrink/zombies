package com.zombies.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Room;
import com.zombies.Zone;

import java.util.HashMap;
import java.util.Random;

public class MapGen {

    public static char[] DIRECTIONS = {'n', 'e', 's', 'w'};

    public static void update(Zone z) {
        // needs to be done during generation, not creation
        if (z.getAdjZones().size() < 8)
            z.findAdjZones();

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
        return b.generateRandomHallway();
    }

    private static Room genRoom(Zone z) {
        Random r = new Random();
        HashMap<String, Box> boxMap = new HashMap<String, Box>();
        for (int i=0; i <= 5; i++) { // try 5 times
            Vector2 boxPosition = z.randomDiscreetPosition(20);
            if (collides(z, boxPosition, C.BOX_SIZE, C.BOX_SIZE) == null) {
                Box b = new Box(boxPosition.x, boxPosition.y);
                b.BMKey = "0,0";
                boxMap.put("0,0", b);
                break;
            }
        }

        if (boxMap.size() == 0)
            return null;

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (boxMap.size() <= roomSize && boxMap.size() > 0) {
            if (true) { // TODO add randomness for perfect or imperfect alignment later
                Object[] boxMapArray = boxMap.values().toArray(); // so we can grab a random box

                // find a box with at least one open side
                Box b;
                do {
                    b = (Box)boxMapArray[r.nextInt(boxMapArray.length)];
                } while (b.getAdjBoxes().size() == 4);

                // find said open side (this can be improved)
                char direction;
                do {
                    direction = DIRECTIONS[r.nextInt(4)];
                } while (b.getAdjBox(direction) != null);

                int[] newBMLocation = b.getBMLocation();

                // rasterize that direction
                Vector2 proposedPosition = new Vector2();
                switch (direction) {
                    case 'n': // top
                        proposedPosition = b.getPosition().cpy().add(0, b.height);
                        newBMLocation[1]++;
                        break;
                    case 'e': // right
                        proposedPosition = b.getPosition().cpy().add(b.width, 0);
                        newBMLocation[0]++;
                        break;
                    case 's': // bottom
                        proposedPosition = b.getPosition().cpy().sub(0, b.height);
                        newBMLocation[1]--;
                        break;
                    case 'w': // left
                        proposedPosition = b.getPosition().cpy().sub(b.width, 0);
                        newBMLocation[0]--;
                        break;
                }
                if (collides(z, proposedPosition, C.BOX_SIZE, C.BOX_SIZE) == null) {
                    Box bb = new Box(proposedPosition.x, proposedPosition.y);
                    bb.BMKey = newBMLocation[0] + "," + newBMLocation[1];
                    boxMap.put(bb.BMKey, bb);
                    associate(bb, boxMap);
                }
            }

            loops++;
            if (loops > roomSize * 4) // catch infinite loops
                break;
        }

        Room room = new Room(boxMap.values());
        genHallway(room);
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

            int oppositeDirection = i/2 < 2 ? i/2 + 2 : i/2 - 2;
            bb.setAdjBox(DIRECTIONS[oppositeDirection], b);
        }
    }

    public static Box collides(Zone z, Vector2 p, float w, float h) {
        for (Zone zone : z.getAdjZonesPlusSelf()) {
            for (Box b : zone.getBoxes()) {
                if (rectOverlap(b, p, w, h))
                    return b;
            }
        }
        return null;
    }

    private static boolean rectOverlap(Box b, Vector2 p, float w, float h) {
        boolean xOverlap = valueInRange(b.x(), p.x, p.x + w) ||
                valueInRange(b.x() + b.width, p.x, p.x + w) ||
                valueInRange(p.x, b.x(), b.x() + b.width) ||
                valueInRange(p.x + w, b.x(), b.x() + b.width);
        boolean yOverlap = valueInRange(b.y(), p.y, p.y + h) ||
                valueInRange(b.y() + b.height, p.y, p.y + h) ||
                valueInRange(p.y, b.y(), b.y() + b.height) ||
                valueInRange(p.y + h, b.y(), b.y() + b.height);
        return xOverlap && yOverlap;
    }

    private static boolean valueInRange(float value, float min, float max) {
        return (value >= min) && (value <= max);
    }
}
