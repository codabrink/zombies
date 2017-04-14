package com.zombies.interfaces.Streets;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;

public interface StreetNode {
    void addConnection(StreetConnection sc);
    boolean checkAvailability(StreetConnection sc);
    Vector2 getPosition();
    float getOrientation();
    Zone getZone();
    float dstFromCenter();
}
