package com.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.Zone;

import java.util.Random;

public class MapGen {
    public static void fillZone(Zone z) {
        Random r = new Random();
        Vector2 boxPosition = new Vector2(r.nextFloat() * C.ZONE_SIZE + z.getPosition().x, r.nextFloat() * C.ZONE_SIZE + z.getPosition().y);
        if (!collides(z, boxPosition)) {
            Box b = new Box(boxPosition.x, boxPosition.y);
        }
    }

    private static boolean collides(Zone z, Vector2 boxPosition) {
        for (Box b : z.getBoxes()) {
            Vector2 position = b.getPosition();
            if (boxPosition.x > position.x && boxPosition.x < position.x + C.BOX_WIDTH) {
                if (boxPosition.y > position.y && boxPosition.y < position.y + C.BOX_HEIGHT) {
                    return true;
                }
            }
        }
        return false;
    }
}
