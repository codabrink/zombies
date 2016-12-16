package com.zombies.map;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.interfaces.IOverlappable;

import java.util.ArrayList;

public class Pathfinding {

    public static ArrayList<Vector2> toPlayer(Vector2 start) {
        ArrayList<Vector2> relays = new ArrayList<>();

        IOverlappable o1 = Zone.getZone(start).checkOverlap(start.x, start.y, 1).iterator().next();

        if (o1.className() == "Box") {

        }

        return relays;
    }

}
