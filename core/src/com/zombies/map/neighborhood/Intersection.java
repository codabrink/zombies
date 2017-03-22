package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;

import java.util.LinkedHashMap;

public class Intersection implements StreetNode {
    public LinkedHashMap<Double, StreetConnection> connections = new LinkedHashMap<>();
    private Vector2 position;

    @Override
    public Vector2 getPosition() { return position; }

    @Override
    public void addConnection(StreetConnection sc) {
        //connections.
    }
}
