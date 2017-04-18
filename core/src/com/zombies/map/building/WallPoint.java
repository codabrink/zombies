package com.zombies.map.building;

import com.badlogic.gdx.math.Vector2;

public class WallPoint {
    public Vector2 point;
    public float height;

    public WallPoint(Vector2 point, float height) {
        this.point  = point;
        this.height = height;
    }
}
