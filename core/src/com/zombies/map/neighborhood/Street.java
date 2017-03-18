package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;

import java.util.LinkedHashSet;

public class Street {
    private LinkedHashSet<StreetSegment> streetSegments = new LinkedHashSet<>();

    public Street(Vector2 p1, Vector2 p2) {
        for (Zone z : Zone.zonesOnLine(p1, p2)) {
            z.addObject(this);


        }
    }
}
