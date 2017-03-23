package com.zombies.util;

import com.badlogic.gdx.math.Vector2;
import com.zombies.abstract_classes.Overlappable;

public class Geom {
    public static final double PIHALF = Math.PI / 2;
    public static final double THRPIHALF = (3 * Math.PI) / 2;
    public static final double TWOPI = 2 * Math.PI;

    public static boolean rectContains(float x, float y, Vector2 p, float w, float h) {
        return inRange(x, p.x, p.x + w) &&
                inRange(y, p.y, p.y + h);
    }

    public static boolean rectOverlap(float x, float y, float w, float h, float x2, float y2, float w2, float h2) {
        if (x >= x2 + w2 || x2 >= x + w)
            return false;
        if (y >= y2 + h2 || y2 >= y + h)
            return false;
        return true;
    }

    public static float distanceOfPointFromLine(Vector2 p1, Vector2 p2, Vector2 p0) {
        double distance = Math.abs((p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x)
                / Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x, 2));
        return (float)distance;
    }

    private static boolean inRange(float value, float a, float b) {
        return value > a && value < b || value < a && value > b;
    }

    // get lineIntersectionPoint point of a line and a box
    public static Vector2 edgeIntersection(Vector2 lp1, Vector2 lp2, Overlappable o) {
        Vector2 position;

        double theta = getAngle(o.getCenter(), lp1);
        double cTheta = Math.atan2(o.getHeight() / 2, o.getWidth() / 2); // corner theta

        U.p("theta: " + theta + ", cTheta: " + cTheta);

        if (Math.abs(theta) < Math.abs(cTheta)) { // right wall
            U.p("Intersecting right wall");
            if ((position = segmentIntersectionPoint(lp1, lp2, o.getCorners()[0], o.getCorners()[3])) != null)
                return position;
        }
        if (theta > 0 && theta > cTheta && theta < Math.PI - cTheta) { // top wall
            U.p("Intersecting top wall");
            if ((position = segmentIntersectionPoint(lp1, lp2, o.getCorners()[0], o.getCorners()[1])) != null)
                return position;
        }
        if (theta < 0 && theta < -cTheta && theta > -(Math.PI - cTheta)) { // bottom wall
            U.p("Intersecting bottom wall");
            if ((position = segmentIntersectionPoint(lp1, lp2, o.getCorners()[2], o.getCorners()[3])) != null)
                return position;
        }
        U.p("Default: Intersecting left wall");
        // otherwise, assume left wall
        if ((position = segmentIntersectionPoint(lp1, lp2, o.getCorners()[1], o.getCorners()[2])) != null)
            return position;

        return null;
    }

    // check if line segment AB intersects segment CD
    public static Vector2 segmentIntersectionPoint(Vector2 a, Vector2 b, Vector2 c, Vector2 d) {
        return segmentIntersectionPoint(a, b, c, d, line(a, b), line(c, d));
    }
    public static Vector2 segmentIntersectionPoint(Vector2 a, Vector2 b, Vector2 c, Vector2 d, float[] ab, float[] cd) {
        Vector2 point = lineIntersectionPoint(ab, cd);
        if (point == null)
            return null; // lines have same slope

        if (!inRange(point.x, a.x, b.x) ||
                !inRange(point.y, a.y, b.y) ||
                !inRange(point.x, c.x, d.x) ||
                !inRange(point.y, c.y, d.y))
            return null; // point is outside of the segment(s)

        return point;
    }

    public static float[] line(Vector2 p1, Vector2 p2) {
        float A = p1.y - p2.y;
        float B = p2.x - p1.x;
        float C = -(p1.x * p2.y - p2.x * p1.y);
        return new float[] {A, B, C};
    }
    // http://stackoverflow.com/questions/20677795/how-do-i-compute-the-intersection-point-of-two-lines-in-python#20679579
    public static Vector2 lineIntersectionPoint(float[] l1, float[] l2) {
        float d = l1[0] * l2[1] - l1[1] * l2[0];
        if (d == 0)
            return null;

        float dx = l1[2] * l2[1] - l1[1] * l2[2];
        float dy = l1[0] * l2[2] - l1[2] * l2[0];
        return new Vector2(dx / d, dy / d);
    }

    public static Vector2 center(Vector2 p1, Vector2 p2) {
        return new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    public static double getAngle(Vector2 p1, Vector2 p2) {
        return Math.atan2(p2.y - p1.y, p2.x - p1.x);
    }

    public static double angleDelta(double a1, double a2) {
        return TWOPI - (Math.abs(a2 - a1) % TWOPI);
    }

    public static Vector2 projectVector(Vector2 v, double angle, float length) {
        return v.cpy().add((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }
}
