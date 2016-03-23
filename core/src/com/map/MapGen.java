package com.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.Room;
import com.zombies.Zone;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class MapGen {
    public static void update(Zone z) {
        // needs to be done during generation, not creation
        if (z.getAdjZones().size() < 8)
            z.findAdjZones();

        if (z.getRooms().size() < z.numRooms && z.roomGenFailureCount < z.numRooms * 2) {
            Room room = genRoom(z, z.randomPosition());
            if (room != null)
                z.addRoom(room);
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
            Room room = genRoom(z, z.randomPosition());
            if (room != null)
                z.addRoom(room);
            else
                break;
        }
    }

    private static Room genRoom(Zone z, Vector2 position) {
        Random r = new Random();
        ArrayList<Box> boxes = new ArrayList<Box>();
        for (int i=0; i <= 5; i++) { // try 5 times
            Vector2 boxPosition = new Vector2(r.nextFloat() * C.ZONE_SIZE + z.getPosition().x, r.nextFloat() * C.ZONE_SIZE + z.getPosition().y);
            if (!collides(z, boxPosition, C.BOX_WIDTH, C.BOX_HEIGHT)) {
                boxes.add(new Box(boxPosition.x, boxPosition.y));
                break;
            }
        }

        if (boxes.size() == 0)
            return null;

        int roomSize = r.nextInt(3) + 30, loops = 0;
        while (boxes.size() <= roomSize && boxes.size() > 0) {
            if (true) { // TODO add randomness for perfect or imperfect alignment later
                Box b = boxes.get(r.nextInt(boxes.size()));
                Vector2 proposedPosition = new Vector2();
                switch (r.nextInt(3)) {
                    case 0: // top
                        proposedPosition = b.getPosition().cpy().add(0, b.height);
                        break;
                    case 1: // right
                        proposedPosition = b.getPosition().cpy().add(b.width, 0);
                        break;
                    case 2: // bottom
                        proposedPosition = b.getPosition().cpy().sub(0, b.height);
                        break;
                    case 3: // left
                        proposedPosition = b.getPosition().cpy().sub(b.width, 0);
                        break;
                }
                if (!collides(z, proposedPosition, C.BOX_WIDTH, C.BOX_HEIGHT) && doesNotDupe(proposedPosition, boxes))
                    boxes.add(new Box(proposedPosition.x, proposedPosition.y));
            }

            loops++;
            if (loops > roomSize * 4) // catch infinite loops
                break;
        }

        return new Room(boxes);
    }

    private static boolean doesNotDupe(Vector2 p, ArrayList<Box> boxes) {
        for (Box b: boxes) {
            if (b.getPosition().dst(p) <= 1f)
                return false;
        }
        return true;
    }

    private static boolean collides(Zone z, Vector2 p, float w, float h) {
        for (Zone zone : z.getAdjZonesPlusSelf()) {
            for (Box b : zone.getBoxes()) {
                if (rectOverlap(b, p, w, h))
                    return true;
            }
        }
        return false;
    }

    private static boolean collides(ArrayList<Box> boxes, Vector2 p, float w, float h) {
        for (Box b: boxes) {
            if (rectOverlap(b, p, w, h))
                return true;
        }
        return false;
    }

    private static boolean rectOverlap(Box b, Vector2 p, float w, float h) {
        boolean xOverlap = valueInRange(b.x(), p.x, p.x + w) ||
                valueInRange(p.x, b.x(), b.x() + b.width);
        boolean yOverlap = valueInRange(b.y(), p.y, p.y + h) ||
                valueInRange(p.y, b.y(), b.y() + b.height);
        return xOverlap && yOverlap;
    }

    private static boolean valueInRange(float value, float min, float max) {
        return (value > min) && (value < max);
    }
}
