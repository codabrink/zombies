package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;
import com.zombies.util.U;

import java.util.HashMap;
import java.util.Random;

public class Generator {
    public static Room genRoom(Building building, int[] bmKey) {
        Vector2 startPos = building.positionOf(bmKey);
        Zone z = Zone.getZone(startPos);
        Random r = new Random();
        HashMap<String, Box> boxMap = building.boxMap;

        Room room = new Room(building);

        if (z.checkOverlap(building.positionOf(bmKey), C.BOX_DIAMETER, C.BOX_DIAMETER, 1) != null)
            return null;

        Box b = new Box(building, room, bmKey);

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (room.boxes.size() <= roomSize) {
            Object[] boxMapArray = room.boxes.toArray();
            int[][] openAdjBMAKeys;

            bmKey = (int[]) U.random(b.getOpenAdjKeys());
            if (bmKey == null)
                break;

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
