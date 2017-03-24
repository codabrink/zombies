package com.zombies.interfaces.Streets;

import com.badlogic.gdx.math.Vector2;

public interface StreetNode {
    public void addConnection(StreetConnection sc);
    public boolean checkAvailability(StreetConnection sc);
    public Vector2 getPosition();
    public int[] getKey();
}
