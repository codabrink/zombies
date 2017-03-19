package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.map.room.Building;

import java.util.LinkedHashSet;

public class Street {
    private LinkedHashSet<StreetSegment> streetSegments = new LinkedHashSet<>();
    private LinkedHashSet<Building> buildings           = new LinkedHashSet<>();
    private final float STREET_WIDTH = 1f;

    public Street(Vector2 p1, Vector2 p2) {
        for (Zone z : Zone.zonesOnLine(p1, p2)) {
            z.addObject(this);


        }
    }
}
