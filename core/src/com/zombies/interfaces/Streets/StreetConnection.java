package com.zombies.interfaces.Streets;

import com.badlogic.gdx.math.Vector2;

public interface StreetConnection {
    public double getAngle();
    public double getAngle(StreetNode sn);
    public Vector2 getP1();
    public Vector2 getP2();
    public float distance(Vector2 p);
}
