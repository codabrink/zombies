package com.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.Room;
import com.zombies.Zone;

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

        for (int i=0;i<=10;i++) {
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

        int roomSize = r.nextInt(5), loops = 0;
        while (boxes.size() <= roomSize && boxes.size() > 0) {
            if (true) { // TODO add randomness for perfect or imperfect alignment later
                Box b = boxes.get(r.nextInt(boxes.size()));
                switch (r.nextInt(3)) {
                    case 0: // top
                        if (!collides(z, b.getPosition().cpy().add(0, b.height), b.width, b.height))
                            boxes.add(new Box(b.getPosition().x, b.getPosition().y + b.height));
                        break;
                    case 1: // right
                        if (!collides(z, b.getPosition().cpy().add(b.width, 0), b.width, b.height))
                            boxes.add(new Box(b.getPosition().x + b.width, b.getPosition().y));
                        break;
                    case 2: // bottom
                        if (!collides(z, b.getPosition().cpy().sub(0, b.height), b.width, b.height))
                            boxes.add(new Box(b.getPosition().x, b.getPosition().y - b.height));
                        break;
                    case 3: // left
                        if (!collides(z, b.getPosition().cpy().sub(b.width, 0), b.width, b.height))
                            boxes.add(new Box(b.getPosition().x - b.width, b.getPosition().y));
                        break;
                }
            }
            loops++;
            if (loops > 10) // catch infinite loops
                break;
        }

        return new Room(boxes);
    }

    private static boolean collides(Zone z, Vector2 boxPosition, float width, float height) {
        for (Zone zone : z.getAdjZonesPlusSelf()) {
            for (Box b : zone.getBoxes()) {
                if (within(boxPosition.x, b.getPosition().x, b.getPosition().x + b.width)) {
                    if (within(boxPosition.y, b.getPosition().y, b.getPosition().y + b.height))
                        return true;
                    else if (within(boxPosition.y + height, b.getPosition().y, b.getPosition().y + b.height))
                        return true;
                } else if (within(boxPosition.x + width, b.getPosition().x, b.getPosition().x + b.width)) {
                    if (within(boxPosition.y, b.getPosition().y, b.getPosition().y + b.height))
                        return true;
                    else if (within(boxPosition.y + height, b.getPosition().y, b.getPosition().y + b.height))
                        return true;
                }
            }
        }
        return false;
    }

    // is a within b and c?
    private static boolean within(float a, float b, float c) {
        return a > b && a < c || a < b && a > c;
    }
}
