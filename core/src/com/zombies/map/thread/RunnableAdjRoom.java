package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.map.room.Box;
import com.zombies.util.Geometry;

import java.util.ArrayList;
import java.util.Random;

public class RunnableAdjRoom implements Runnable {
    private Box adjBox;

    public RunnableAdjRoom(Box adjBox) {
        this.adjBox = adjBox;
    }

    @Override
    public void run() {
        genRoom();
    }

    private void genRoom() {
        if (C.DEBUG == true && adjBox == null)
            System.out.println("ERROR! RunnableAdjRoom: has not been supplied an adjBox");

        // get a random open direction
        ArrayList<Integer> openDirections = adjBox.getOpenDirections();
        int direction = openDirections.get((new Random()).nextInt(openDirections.size()));

        Vector2 startPos = adjBox.getCenter();
        startPos = Geometry.projectVector(startPos, Math.toRadians(direction), C.BOX_DIAMETER);
        startPos.sub(C.BOX_DIAMETER / 2, C.BOX_DIAMETER / 2);

        Generator.zoneRoom(Generator.genRoom(startPos));
    }
}
