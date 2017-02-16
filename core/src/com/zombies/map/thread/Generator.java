package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;

import java.util.HashMap;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class Generator {
    public static Room genRoom(Building building, int[] bmKey) {
        Vector2 startPos = building.positionOf(bmKey);
        Zone z = Zone.getZone(startPos);
        Random r = new Random();
        HashMap<int[], Box> boxMap = building.boxMap;

        Room room = new Room(building);

        Box b = new Box(building, room, bmKey);
        boxMap.put(bmKey, b);

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (room.boxes.size() <= roomSize) {
            Object[] boxMapArray = room.boxes.toArray();
            int[][] openAdjBMAKeys;

            do {
                b = (Box) boxMapArray[r.nextInt(boxMapArray.length)];
                openAdjBMAKeys = b.getOpenAdjBMAKeys();
            } while (openAdjBMAKeys.length == 0);

            bmKey = openAdjBMAKeys[r.nextInt(openAdjBMAKeys.length)];

            if (z.checkOverlap(building.positionOf(bmKey), C.BOX_DIAMETER, C.BOX_DIAMETER, 1) == null)
                new Box(building, room, bmKey);

            loops++;
            if (loops > roomSize * 4) // catch infinite loops
                break;
        }

        room.finalize();
        System.out.println("generator - finalized a room");
        return room;
    }
}
