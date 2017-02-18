package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.map.room.Box;
import com.zombies.util.U;

public class RunnableAdjRoom implements Runnable {
    private Box adjBox;

    public RunnableAdjRoom(Box adjBox) {
        this.adjBox = adjBox;
    }

    @Override
    public void run() {
        if (C.DEBUG == true && adjBox == null)
            System.out.println("ERROR! RunnableAdjRoom: has not been supplied an adjBox");

        if (adjBox.getBuilding().threadLocked == true)
            return;

        int[] bmKey = (int[]) U.random(adjBox.getOpenAdjKeys());

        if (bmKey == null)
            return;

        adjBox.getBuilding().threadLocked = true;

        Vector2 position = adjBox.getBuilding().positionOf(bmKey);
        if (adjBox.getZone().checkOverlap(position, C.BOX_DIAMETER, C.BOX_DIAMETER, 1) == null) {
            System.out.println("Generating " + bmKey[0] + ","+bmKey[1]);
            Generator.genRoom(adjBox.getBuilding(), bmKey);
        }

        adjBox.getBuilding().threadLocked = false;
    }
}
