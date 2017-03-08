package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.interfaces.Gridable;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.util.U;

public class RunnableAdjRoom implements Runnable {
    private Gridable adjGridable;

    public RunnableAdjRoom(Gridable adjGridable) {
        this.adjGridable = adjGridable;
    }

    @Override
    public void run() {
        if (C.DEBUG == true && adjGridable == null)
            System.out.println("ERROR! RunnableAdjRoom: has not been supplied an adjBox");

        if (adjGridable.getBuilding().threadLocked == true)
            return;


        int[] bmKey = (int[])U.random(adjGridable.getOpenAdjKeys());

        if (bmKey == null)
            return;

        Building building = adjGridable.getBuilding();
        building.threadLocked = true;

        if (building.checkOverlap(bmKey) == null)
            Generator.genFullBuilding(building.positionOf(bmKey).add(C.GRID_HALF_SIZE, C.GRID_HALF_SIZE));

        adjGridable.getBuilding().threadLocked = false;
    }
}
