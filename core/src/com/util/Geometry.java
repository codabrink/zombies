package com.util;

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
        return (value > min) && (value < max);
    }
}
