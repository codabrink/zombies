package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.lib.math.M;

import java.util.LinkedHashMap;

public class Intersection implements StreetNode {
    public final static double minAngleDelta = Math.PI / 8;

    private StreetSystem streetSystem;
    private Vector2      position;
    private float        dstFromCenter, orientation;

    private Zone zone;
    public LinkedHashMap<Float, StreetConnection> connections = new LinkedHashMap<>();

    public static StreetNode createIntersection(StreetSystem ss, Vector2 p) {
        return new Intersection(ss, p, (float) M.getAngle(ss.getCenter(), D.player().getPosition()));
    }
    public static StreetNode createIntersection(StreetSystem ss, Vector2 p, StreetNode sn) {
        return null;
    }

    private Intersection(StreetSystem ss, Vector2 p, float o) {
        position      = p;
        streetSystem  = ss;
        orientation   = o;

        dstFromCenter = p.dst(ss.getCenter());
        zone          = Zone.getZone(p);
        streetSystem.addNode(this);
    }

    public void compile() {

    }

    @Override
    public Vector2 getPosition() { return position; }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public float dstFromCenter() {
        return dstFromCenter;
    }

    @Override
    public void addConnection(StreetConnection connection) {
        connections.put((float) connection.getAngle(this), connection);
    }

    @Override
    public boolean checkAvailability(StreetConnection connection) {
        for (StreetConnection sc : connections.values())
            if (M.angleDelta(sc.getAngle(this), connection.getAngle(this)) < minAngleDelta)
                return false;
        return true;
    }
}
