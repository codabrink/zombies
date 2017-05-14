package com.zombies.map.building;

import com.badlogic.gdx.math.Vector2;

public class WallPoint {
    public Vector2 p1;
    public float height, length;

    public WallPoint(Vector2 p1, float height, float length) {
        this.p1 = p1;
        this.height = height;
        this.length = length;
    }
}
