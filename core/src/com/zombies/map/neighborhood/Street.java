package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.overlappable.Overlappable;
import com.zombies.overlappable.PolygonOverlappable;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.map.building.Building;
import com.zombies.lib.math.M;
import com.zombies.lib.math.LineSegment;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Street implements StreetConnection {
    private static final float DEFAULT_RADIUS = 10f;

    private StreetSystem streetSystem;
    private LinkedHashSet<StreetSegment> streetSegments = new LinkedHashSet<>();
    private LinkedHashSet<Building> buildings           = new LinkedHashSet<>();

    private float perseverance;
    private LinkedList<StreetNode> intersections = new LinkedList<>();

    public float angle, radius;

    public static Street createStreet(StreetSystem ss, Vector2 p1, float angle, float length, float radius, float perseverance) {
        Vector2 p2 = M.projectVector(p1, angle, length);
        return createStreet(ss, p1, p2, radius, perseverance);
    }
    public static Street createStreet(StreetSystem ss, Vector2 p1, Vector2 p2, float radius, float perseverance) {
        if (radius == 0) radius = DEFAULT_RADIUS;
        if (perseverance == 0) perseverance = 0.5f;

        Intersection i1 = Intersection.createIntersection(ss, p1);
        Intersection i2 = Intersection.createIntersection(ss, p2);
        if (i1 == null || i2 == null) return null;

        PolygonOverlappable polygonOverlappable = new PolygonOverlappable(M.lineToCorners(p1, p2, radius));

        for (Zone z : Zone.zonesOnLine(p1, p2))
            for (Overlappable o : z.checkOverlap(polygonOverlappable, 0, null))
                return null;

        return new Street(ss, i1, i2, polygonOverlappable.getCorners());
    }


    private Street(StreetSystem ss, StreetNode n1, StreetNode n2, Vector2[] corners, ) {
        intersections.push(n1);
        intersections.push(n2);

        angle = (float) M.getAngle(n1.getPosition(), n2.getPosition());

        n1.addConnection(this);
        n2.addConnection(this);

        streetSystem = ss;
        streetSystem.addConnection(this);

        LineSegment lineSegment = new LineSegment(p1, p2);
        LineSegment reverseLineSegment = new LineSegment(p2, p1);

        for (Zone z : Zone.zonesOnLine(p1, p2)) {
            z.addObject(this);

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

    private generateStreetSegments() {
        
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
