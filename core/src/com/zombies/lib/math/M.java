package com.zombies.lib.math;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Geom.Line;

public class M {
    public static final float PIHALF    = (float) (Math.PI / 2f);
    public static final float THRPIHALF = (float) (3f * Math.PI) / 2f;
    public static final float TWOPI     = (float) (2f * Math.PI);
    public static final float PI34      = (float) (Math.PI * (3f / 4f));
    public static final float PI14      = (float) (Math.PI * (1f / 4f));

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

    public static boolean inRange(float value, float a, float b) {
        return value > a && value < b || value < a && value > b;
    }
    public static boolean inRangeInclusive(float value, float a, float b) {
        return value >= a && value <= b || value <= a && value >= b;
    }

    public static Vector2 lineIntersectionPoint(Line a, Line b) {
        Vector2 point = lineIntersectionPoint(a.getFormula(), b.getFormula());
        if (point == null || !a.inRange(point) || !b.inRange(point))
            return null;
        return point;
    }
    public static Vector2 lineIntersectionPointInclusive(Line a, Line b) {
        Vector2 point = lineIntersectionPoint(a.getFormula(), b.getFormula());
        if (point == null || !a.inRangeInclusive(point) || !b.inRangeInclusive(point))
            return null;
        return point;
    }

    public static Vector2[] lineToCorners(LineSegment l, float r) {
        return lineToCorners(l.a, l.b, r);
    }
    public static Vector2[] lineToCorners(Vector2 a, Vector2 b, float r) {
        double angle = getAngle(a, b);
        return new Vector2[]{
                M.projectVector(a, angle - M.PIHALF, r),
                M.projectVector(b, angle - M.PIHALF, r),
                M.projectVector(b, angle + M.PIHALF, r),
                M.projectVector(a, angle + M.PIHALF, r)
        };
    }

    public static float[] line(Vector2 p1, Vector2 p2) {
        float A = p1.y - p2.y;
        float B = p2.x - p1.x;
        float C = p1.x * p2.y - p2.x * p1.y;
        return new float[] {A, B, -C};
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

    public static Vector2 projectVector$(Vector2 v, double angle, float length) {
        return v.add((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }
    public static Vector2 projectVector(Vector2 v, double angle, float length) {
        return projectVector$(v.cpy(), angle, length);
    }
}
