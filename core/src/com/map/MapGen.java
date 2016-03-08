package com.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.Room;
import com.zombies.Zone;

import java.util.ArrayList;
import java.util.Random;

public class MapGen {
    public static void fillZone(Zone z) {
        Random r = new Random();
        genRoom(z, z.randomPosition());
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

        int roomSize = r.nextInt(5), loops = 0;
        while (boxes.size() <= roomSize) {
            if (true) { // TODO add randomness for perfect or imperfect alignment later
                Box b = boxes.get(r.nextInt(boxes.size()));
                switch (r.nextInt(3)) {
                    case 0: // top
                        if (!collides(z, b.getPosition().cpy().sub(0, b.height), b.width, b.height))
                            boxes.add(new Box(b.getPosition().x, b.getPosition().y - b.height));
                        break;
                    case 1: // right
                        if (!collides(z, b.getPosition().cpy().add(b.width, 0), b.width, b.height))
                            boxes.add(new Box(b.getPosition().x + b.width, b.getPosition().y));
                        break;
                    case 2: // bottom
                        if (!collides(z, b.getPosition().cpy().add(0, b.height), b.width, b.height))
                            boxes.add(new Box(b.getPosition().x, b.getPosition().y + b.height));
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
        for (Box b : z.getBoxes()) {
            Vector2 position = b.getPosition();
            if (boxPosition.x > position.x && boxPosition.x < position.x + C.BOX_WIDTH)
                if (boxPosition.y > position.y && boxPosition.y < position.y + C.BOX_HEIGHT)
                    return true;
        }
        return false;
    }
}
