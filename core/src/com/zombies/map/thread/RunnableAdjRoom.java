package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;
import com.zombies.util.Geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RunnableAdjRoom implements Runnable {
    private Box adjBox;

    public RunnableAdjRoom(Box adjBox) {
        this.adjBox = adjBox;
    }

    @Override
    public void run() {
        if (C.DEBUG == true && adjBox == null)
            System.out.println("ERROR! RunnableAdjRoom: has not been supplied an adjBox");

        // get a random open direction
        ArrayList<Integer> openDirections = adjBox.getOpenDirections();
        int direction = openDirections.get((new Random()).nextInt(openDirections.size()));
        int[] bmKey = Building.directionToBMKey(adjBox.getBmKey(), direction);

        Vector2 position = adjBox.getBuilding().positionOf(bmKey);
        if (adjBox.getZone().checkOverlap(position, C.BOX_DIAMETER, C.BOX_DIAMETER, 1) == null)
            Generator.genRoom(adjBox.getBuilding(), bmKey);
    }
}
