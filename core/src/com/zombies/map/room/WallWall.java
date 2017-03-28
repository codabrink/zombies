package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;

public class WallWall extends Wall {
    public WallWall(Vector2 p1, Vector2 p2, Building b) {
        super(p1, p2, b);

        points.add(new WallPoint(p1, 1));
        points.add(new WallPoint(p2, 0));
    }
}
