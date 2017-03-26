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
    private int[] key;
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

    public static void populateBox(StreetSystem ss, Vector2 p, float w, float h, float resolution) {
        p = p.cpy();
        for (float x = p.x; x <= p.x + w; x += resolution) {
            for (float y = p.y; y <= p.y + h; y += resolution) {
                StreetNode node = ss.getClosestNode(new Vector2(x, y), 1);
                if (node != null) continue;

                StreetNode row = ss.closestOnRow(p, 1);
                StreetNode col = ss.closestOnCol(p, 1);

                if (row != null)
                    p.set(p.x, row.getPosition().y);
                if (col != null)
                    p.set(col.getPosition().x, p.y);

                node = new Intersection(ss, p);

                if (row != null)
                    new Street(ss, node, row);
                if (col != null)
                    new Street(ss, node, col);
            }
        }
    }

    protected Intersection(StreetSystem ss, Vector2 p) {
        position     = p;
        key          = ss.keyOf(position);
        streetSystem = ss;
        streetSystem.addNode(this);
    }

    public void compile() {

    }

    @Override
    public Vector2 getPosition() { return position; }

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
