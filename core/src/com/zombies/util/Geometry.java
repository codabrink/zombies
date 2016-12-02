package com.zombies.util;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Overlappable;
import java.util.ArrayList;

public class Geometry {
    public static boolean rectContains(float x, float y, Vector2 p, float w, float h) {
        return valueInRange(x, p.x, p.x + w) &&
                valueInRange(y, p.y, p.y + h);
    }

    public static boolean rectOverlap(float x, float y, float w, float h, float x2, float y2, float w2, float h2) {
        if (x >= x2 + w2 || x2 >= x + w)
            return false;
        if (y >= y2 + h2 || y2 >= y + h)
            return false;
        return true;
    }

    private static boolean valueInRange(float value, float min, float max) {
        return (value > min) && (value < max);
    }

    // get intersection point of a line and a box
    public static Vector2 edgeIntersection(Vector2 lp1, Vector2 lp2, Vector2 p, float w, float h) {
        Vector2 position;

        // right wall
        if ((position = intersectPoint(lp1, lp2, p.cpy().add(w, 0), p.cpy().add(w, h))) != null)
            return position;
        // top wall
        if ((position = intersectPoint(lp1, lp2, p.cpy().add(0, h), p.cpy().add(w, h))) != null)
            return position;
        // left wall
        if ((position = intersectPoint(lp1, lp2, p, p.cpy().add(0, h))) != null)
            return position;
        // bottom wall
        if ((position = intersectPoint(lp1, lp2, p, p.cpy().add(w, 0))) != null)
            return position;

        return null;
    }

    public static Vector2 intersectPoint(Vector2 l1p1, Vector2 l1p2, Vector2 l2p1, Vector2 l2p2) {
        return intersectPoint(l1p1.x, l1p1.y, l1p2.x, l1p2.y, l2p1.x, l2p1.y, l2p2.x, l2p2.y);
    }

    public static Vector2 intersectPoint(float l1p1x, float l1p1y, float l1p2x, float l1p2y, float l2p1x, float l2p1y, float l2p2x, float l2p2y) {
        float A1 = l1p2y - l1p1y;
        float B1 = l1p2x - l1p1x;
        float C1 = A1 * l1p1x + B1 * l1p1y;

        float A2 = l2p2y - l2p1y;
        float B2 = l2p2x - l2p1x;
        float C2 = A2 * l2p1x + B2 * l2p1y;

        float delta = A1*B2 - A2*B1;
        if (delta == 0) {
            return null; // lines are parallel
        }

        return new Vector2((B2*C1 - B1*C2)/delta, (A1*C2 - A2*C1)/ delta);
    }

    public static Overlappable checkOverlap(float x, float y, float w, float h, ArrayList<Overlappable> overlappables) {
        for (Overlappable o: overlappables) {
            if (o.overlaps(x, y, w, h))
                return o;
        }
        return null;
    }

    public static double getAngleFromPoints(Vector2 p1, Vector2 p2) {
        float dx = p2.x - p1.x, dy = p2.y - p1.y;
        return Math.atan2(dy, dx);
    }

    public static Vector2 projectVector(Vector2 v, double angle, float length) {
        return v.cpy().add((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }
}
