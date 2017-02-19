package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;

public class WallPoint {
    private Vector2 point;
    private float height;

    public WallPoint(Vector2 point, float height) {
        this.point  = point;
        this.height = height;
    }

    public Vector2 getPoint()  { return point; }
    public float   getHeight() { return height; }
}
