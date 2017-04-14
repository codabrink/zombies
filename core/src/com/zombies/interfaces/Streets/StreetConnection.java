package com.zombies.interfaces.Streets;

import com.badlogic.gdx.math.Vector2;

public interface StreetConnection {
    double getAngle();
    double getAngle(StreetNode sn);
    float getLength();
    Vector2 getP1();
    Vector2 getP2();
    float distance(Vector2 p);
}
