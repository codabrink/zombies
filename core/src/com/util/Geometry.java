package com.util;

import com.badlogic.gdx.math.Vector2;
import com.interfaces.Overlappable;

import java.util.ArrayList;

/**
 * Created by coda on 4/2/16.
 */
public class Geometry {

    public static boolean rectOverlap(float x, float y, float w, float h, float x2, float y2, float w2, float h2) {
        boolean xOverlap = valueInRange(x, x2, x2 + w2) ||
                valueInRange(x2, x, x + w);
        boolean yOverlap = valueInRange(y, y2, y2 + h2) ||
                valueInRange(y2, y, y + h);
        return xOverlap && yOverlap;
    }

    private static boolean valueInRange(float value, float min, float max) {
        return (value >= min) && (value <= max);
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
}
