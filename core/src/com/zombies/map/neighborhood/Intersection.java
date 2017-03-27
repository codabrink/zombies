package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.util.G;

import java.util.LinkedHashMap;

public class Intersection implements StreetNode {
    public final static double minAngleDelta = Math.PI / 8;

    private StreetSystem streetSystem;
    private Vector2      position;
    private int[] key;
    private Zone zone;
    public LinkedHashMap<Double, StreetConnection> connections = new LinkedHashMap<>();

    public static StreetNode createIntersection(StreetSystem ss, Vector2 p) {
        StreetNode node = ss.getNode(p);
        if (node != null)
            return node;

        return new Intersection(ss, p);
    }
    public static StreetNode createIntersection(StreetSystem ss, Vector2 p, StreetNode sn) {
        StreetNode node = ss.getNode(p);
        if (node != null)
            return node;

        Intersection intersection = new Intersection(ss, p);
        Street.createStreet(ss, sn, intersection);
        return intersection;
    }


    protected Intersection(StreetSystem ss, Vector2 p) {
        position     = p;
        key          = ss.keyOf(position);
        streetSystem = ss;
        zone         = Zone.getZone(p);
        streetSystem.addNode(this);
    }

    public void compile() {

    }

    @Override
    public Vector2 getPosition() { return position; }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public int[] getKey() {
        return key;
    }

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
