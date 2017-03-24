package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.util.G;

import java.util.LinkedHashMap;

public class Intersection implements StreetNode {
    public final static double minAngleDelta = Math.PI / 8;

    private StreetSystem streetSystem;
    private Vector2      position;
    public LinkedHashMap<Double, StreetConnection> connections = new LinkedHashMap<>();

    public static Intersection createIntersection(StreetSystem ss, Vector2 p) {
        return new Intersection(ss, p);
    }
    public static Intersection createIntersection(StreetSystem ss, StreetNode sn, Vector2 p) {
        Intersection intersection = new Intersection(ss, p);
        // TODO: finish
        return intersection;
    }


    protected Intersection(StreetSystem ss, Vector2 p) {
        position     = p;
        streetSystem = ss;
        streetSystem.addNode(this);
    }

    public void compile() {

    }

    @Override
    public Vector2 getPosition() { return position; }

    @Override
    public void addConnection(StreetConnection connection) {
        connections.put(connection.getAngle(this), connection);
    }

    @Override
    public boolean checkAvailability(StreetConnection connection) {
        for (StreetConnection sc : connections.values())
            if (G.angleDelta(sc.getAngle(this), connection.getAngle(this)) < minAngleDelta)
                return false;
        return true;
    }
}
