package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.lib.math.M;

import java.util.LinkedHashMap;

public class Intersection implements StreetNode {
    public final static float MIN_DELTA_ANGLE = (float) (Math.PI / 8);
    public static final float MIN_INTERSECTION_DISTANCE = 1f;

    private StreetSystem streetSystem;
    private Vector2      position;
    private float        dstFromCenter, angle;

    private Zone zone;
    public LinkedHashMap<Float, StreetConnection> connections = new LinkedHashMap<>();

    public static Intersection createIntersection(StreetSystem ss, Vector2 p) {
        for (StreetNode sn : ss.getNodes())
            if (sn.getPosition().dst(p) < MIN_INTERSECTION_DISTANCE)
                return null;
        return new Intersection(ss, p, (float) M.getAngle(ss.getCenter(), D.player().getPosition()));
    }

    private Intersection(StreetSystem ss, Vector2 p, float angle) {
        position      = p;
        streetSystem  = ss;
        this.angle = angle;

        dstFromCenter = p.dst(ss.getCenter());
        zone          = Zone.getZone(p);
    }

    public void compile() {
        streetSystem.addNode(this);
    }

    @Override
    public Vector2 getPosition() { return position; }

    public float getAngle() {
        return angle;
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
            if (M.angleDelta(sc.getAngle(this), connection.getAngle(this)) < MIN_DELTA_ANGLE)
                return false;
        return true;
    }
}
