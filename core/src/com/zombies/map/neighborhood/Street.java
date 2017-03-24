package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.map.room.Building;
import com.zombies.util.Bounds2;
import com.zombies.util.G;

import java.util.LinkedHashSet;

public class Street implements StreetConnection {
    public static final float RADIUS = 1f;

    private StreetSystem streetSystem;
    private LinkedHashSet<StreetSegment> streetSegments = new LinkedHashSet<>();
    private LinkedHashSet<Building> buildings           = new LinkedHashSet<>();
    private StreetNode n1, n2;
    private Vector2 p1, p2;
    private double angle;
    private boolean compiled = false;

    public static Street createStreet(StreetSystem ss, StreetNode n1, StreetNode n2) {
        return new Street(ss, n1, n2);
    }

    protected Street(StreetSystem ss, StreetNode n1, StreetNode n2) {
        p1 = n1.getPosition();
        p2 = n2.getPosition();

        angle = G.getAngle(p1, p2);
        this.n1 = n1;
        this.n2 = n2;

        n1.addConnection(this);
        n2.addConnection(this);

        streetSystem = ss;
        streetSystem.addConnection(this);

        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;

        for (Zone z : Zone.zonesOnLine(p1, p2)) {
            z.addPendingObject(this);
            Bounds2 bounds = Bounds2.crop(z.bounds, p1, dx, dy);
            z.addObject(new StreetSegment(p1, p1.cpy().add(bounds.w, bounds.h), angle));
        }
    }

    public void compile() {
        compiled = true;
    }
    public boolean isCompiled() { return compiled; }
    public Vector2 getP1() { return p1; }
    public Vector2 getP2() { return p2; }

    @Override
    public float distance(Vector2 p) {
        return G.distanceOfPointFromLine(p1, p2, p);
    }
    @Override
    public double getAngle() { return angle; }
    @Override
    public double getAngle(StreetNode sn) {
        if (sn == n2)
            return (angle + G.PIHALF) % G.TWOPI;
        return  angle;
    }
}
