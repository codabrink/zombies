package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.map.room.Building;
import com.zombies.util.Bounds2;
import com.zombies.util.G;
import com.zombies.util.LineSegment;

import java.util.LinkedHashSet;

public class Street implements StreetConnection {
    public static final float RADIUS = 3f;

    private StreetSystem streetSystem;
    private LinkedHashSet<StreetSegment> streetSegments = new LinkedHashSet<>();
    private LinkedHashSet<Building> buildings           = new LinkedHashSet<>();
    private StreetNode n1, n2;
    private Vector2 p1, p2;
    private double angle;
    private boolean compiled = false;

    public static Street createStreet(StreetSystem ss, StreetNode n1, StreetNode n2) {
        Vector2 p1 = n1.getPosition();
        Vector2 p2 = n2.getPosition();
        double angle = G.getAngle(p1, p2);
        Overlappable overlappable = new Overlappable(getCorners(p1, p2, angle, RADIUS));

        for (Zone z : Zone.zonesOnLine(p1, p2))
            if (z.checkOverlap(overlappable, 0, null) != null)
                return null;

        return new Street(ss, n1, n2, angle, overlappable.getCorners());
    }

    public static Vector2[] getCorners(Vector2 p1, Vector2 p2, double angle, float radius) {
        return new Vector2[]{
                G.projectVector(p1, angle - G.PIHALF, radius),
                G.projectVector(p2, angle - G.PIHALF, radius),
                G.projectVector(p2, angle + G.PIHALF, radius),
                G.projectVector(p1, angle + G.PIHALF, radius)
        };
    }

    private Street(StreetSystem ss, StreetNode n1, StreetNode n2, double angle, Vector2[] corners) {
        p1 = n1.getPosition();
        p2 = n2.getPosition();

        this.angle = angle;
        this.n1 = n1;
        this.n2 = n2;

        n1.addConnection(this);
        n2.addConnection(this);

        streetSystem = ss;
        streetSystem.addConnection(this);

        LineSegment lineSegment = new LineSegment(p1, p2);
        LineSegment reverseLineSegment = new LineSegment(p2, p1);

        for (Zone z : Zone.zonesOnLine(p1, p2)) {
            z.addPendingObject(this);

            Vector2 i1, i2;
            i1 = z.lineIntersect(lineSegment);
            i2 = z.lineIntersect(reverseLineSegment);

            if (i1 == null)
                i1 = p1;
            if (i2 == null)
                i2 = p2;

            if (i1.equals(i2)) { // if this is true; ONE of the points is contained by the zone
                if (z.contains(i1))
                    i1 = p1;
                else
                    i2 = p2;
            }

            StreetSegment.createStreetSegment(this, i1, i2, angle);
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
