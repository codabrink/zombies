package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Modelable;

public class WallWall extends Wall {

    public WallWall(Vector2 p1, Vector2 p2, Modelable m) {
        super(p1, p2, m);

        points.add(new WallPoint(p1, 1));
        points.add(new WallPoint(p2, 0));

        genSegmentsFromPoints();
    }
}
