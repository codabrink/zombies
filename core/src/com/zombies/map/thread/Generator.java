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
    public static Room genRoom(Building building, String startBMKey) {
        String[] sKeys = startBMKey.split(",");
        int[] bmKeys   = { parseInt(sKeys[0]), parseInt(sKeys[1]) };

        Vector2 startPos = building.positionOf(bmKeys[0], bmKeys[1]);
        Zone z = Zone.getZone(startPos);
        Random r = new Random();
        HashMap<String, Box> boxMap = building.boxMap;

        Room room = new Room(building);

        Box b = new Box(building, room, startBMKey);
        boxMap.put(startBMKey, b);

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (room.boxes.size() <= roomSize) {
            Object[] boxMapArray = room.boxes.toArray();
            int[][] openAdjBMAKeys;

            do {
                b = (Box) boxMapArray[r.nextInt(boxMapArray.length)];
                openAdjBMAKeys = b.getOpenAdjBMAKeys();
            } while (openAdjBMAKeys.length == 0);

            int[] bmaKey = openAdjBMAKeys[r.nextInt(openAdjBMAKeys.length)];

            if (z.checkOverlap(building.positionOf(bmaKey[0], bmaKey[1]), C.BOX_DIAMETER, C.BOX_DIAMETER, 1) == null)
                new Box(building, room, bmaKey[0]+","+bmaKey[1]);

            loops++;
            if (loops > roomSize * 4) // catch infinite loops
                break;
        }

        room.finalize();
        return room;
    }
}
