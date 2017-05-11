package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.overlappable.PolygonOverlappable;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.map.building.Building;
import com.zombies.lib.math.M;
import com.zombies.lib.math.LineSegment;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Street implements StreetConnection {
    public static final float RADIUS = 10f;

    private StreetSystem streetSystem;
    private LinkedHashSet<StreetSegment> streetSegments = new LinkedHashSet<>();
    private LinkedHashSet<Building> buildings           = new LinkedHashSet<>();

    private float perseverance;
    private LinkedList<StreetNode> streetNodes = new LinkedList<>();

    private float angle, length;

    public static Street createStreet(StreetSystem ss, Vector2 p, float angle, float length) {



        Vector2 p1 = n1.getPosition();
        Vector2 p2 = n2.getPosition();
        float angle = (float) M.getAngle(p1, p2);
        PolygonOverlappable polygonOverlappable = new PolygonOverlappable(getCorners(p1, p2, angle, RADIUS));

        for (Zone z : Zone.zonesOnLine(p1, p2))
            if (z.checkOverlap(polygonOverlappable, 0, null) != null)
                return null;

        return new Street(ss, n1, n2, angle, polygonOverlappable.getCorners());
    }

    public static Vector2[] getCorners(Vector2 p1, Vector2 p2, double angle, float radius) {
        return new Vector2[]{
                M.projectVector(p1, angle - M.PIHALF, radius),
                M.projectVector(p2, angle - M.PIHALF, radius),
                M.projectVector(p2, angle + M.PIHALF, radius),
                M.projectVector(p1, angle + M.PIHALF, radius)
        };
    }

    private Street(StreetSystem ss, StreetNode n1, StreetNode n2, float angle, Vector2[] corners) {
        p1 = n1.getPosition();
        p2 = n2.getPosition();

        this.angle = angle;
        angle      = p1.dst(p2);
        this.n1    = n1;
        this.n2    = n2;

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
        return M.distanceOfPointFromLine(p1, p2, p);
    }
    @Override
    public double getAngle() { return angle; }
    @Override
    public double getAngle(StreetNode sn) {
        if (sn == n2)
            return (angle + M.PIHALF) % M.TWOPI;
        return  angle;
    }

    @Override
    public float getLength() {
        return length;
    }
}
