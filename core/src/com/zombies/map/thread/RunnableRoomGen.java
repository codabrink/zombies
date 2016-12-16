package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.map.room.Box;
import com.zombies.C;
import com.zombies.map.room.Room;
import com.zombies.Zone;
import com.zombies.map.MapGen;

import java.util.HashMap;
import java.util.Random;

public class RunnableRoomGen implements Runnable {
    private Vector2 startPosition;
    private volatile Room room;

    public RunnableRoomGen() {
    }

    @Override
    public void run() {
        genRoom(startPosition);
    }

    public void setStartPosition(Vector2 startPosition) {
        this.startPosition = startPosition;
    }
    public Room getRoom() {
        return room;
    }

    private void genRoom(Vector2 boxPosition) {
        Zone z = Zone.getZone(boxPosition);
        Random r = new Random();
        HashMap<String, Box> boxMap = new HashMap<>();

        Box b = new Box(boxPosition.x, boxPosition.y);
        b.BMKey = "0,0";
        boxMap.put("0,0", b);

        // if it failed to find an open position
        if (boxMap.size() == 0)
            return;

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (boxMap.size() <= roomSize) {
            Object[] boxMapArray = boxMap.values().toArray(); // so we can grab a random box

            // find a box with at least one open side
            do {
                b = (Box)boxMapArray[r.nextInt(boxMapArray.length)];
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

        Room room = new Room(boxMap);
        this.room = room;
    }
}
