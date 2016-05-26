package com.zombies.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zombies.interfaces.Overlappable;

import java.util.ArrayList;

/**
 * Created by coda on 4/2/16.
 */
public class Geometry {

    public static OverlapResult.OverlapType rectOverlap(float x, float y, float w, float h, float x2, float y2, float w2, float h2) {
        Array<Vector2> points = new Array<Vector2>(4);
        points.add(new Vector2(x + w, y + h));
        points.add(new Vector2(x, y + h));
        points.add(new Vector2(x, y));
        points.add(new Vector2(x + w, y));

        for (Vector2 p: points) {
            if (pointInRectangle(p, x2, y2, w2, h2))
                points.removeValue(p, true);
        }

        if (points.get(0) == null && points.get(1) == null && points.get(2) == null && points.get(3) == null)
            return OverlapResult.OverlapType.FULL;
        if (points.get(0) == null && points.get(1) == null)
            return OverlapResult.OverlapType.HORIZ_TOP;
        if (points.get(2) == null && points.get(2) == null)
            return OverlapResult.OverlapType.HORIZ_BOTTOM;
        if (points.get(0) == null && points.get(3) == null)
            return OverlapResult.OverlapType.VERT_RIGHT;
        if (points.get(1) == null && points.get(2) == null)
            return OverlapResult.OverlapType.VERT_LEFT;

        for (int i = 0; i < points.size; i ++)
            if (points.get(i) == null)
                return OverlapResult.OverlapType.CORNER;

        return OverlapResult.OverlapType.NONE;
}

    private static boolean pointInRectangle(Vector2 point, float x, float y, float w, float h) {
        return point.x > x &&
                point.x < x + w &&
                point.y > y &&
                point.y < y + h;
    }

    private static boolean valueInRange(float value, float min, float max) {
        return value > min && value < max;
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

    public static OverlapResult checkOverlap(float x, float y, float w, float h, ArrayList<Overlappable> overlappables) {
        for (Overlappable o: overlappables) {
            OverlapResult or = o.overlaps(x, y, w, h);
            if (or.overlapType != OverlapResult.OverlapType.NONE)
                return or;
        }
        return new OverlapResult(null, OverlapResult.OverlapType.NONE);
    }

    public static Vector2 edgeOfRectangle(Vector2 center, float width, float height, double theta) {
        double twoPI = Math.PI * 2;
        while (theta < -Math.PI)
            theta += twoPI;
        while (theta > Math.PI)
            theta -= twoPI;

        // ref: http://stackoverflow.com/questions/4061576/finding-points-on-a-rectangle-at-a-given-angle

        double rectAtan = Math.atan2(height, width);
        double tanTheta = Math.tan(theta);

        int region;
        if ((theta > -rectAtan) && (theta <= rectAtan))
            region = 1;
        else if ((theta > rectAtan) && theta <= (Math.PI - rectAtan))
            region = 2;
        else if ((theta > (Math.PI - rectAtan)) || (theta <= -(Math.PI - rectAtan)))
            region = 3;
        else
            region = 4;

        Vector2 edgePoint = center.cpy();
        float xFactor = 1;
        float yFactor = 1;

        switch (region) {
            case 1: yFactor = -1; break;
            case 2: yFactor = -1; break;
            case 3: xFactor = -1; break;
            case 4: xFactor = -1; break;
        }

        if (region == 1 || region == 3)
            edgePoint.add(xFactor * (width / 2), (float)(yFactor * (width / 2) * tanTheta));
        else
            edgePoint.add((float)(xFactor * (width / (2 * tanTheta))), yFactor * width / 2);

        return edgePoint;
    }

    public static double getAngleFromPoints(Vector2 p1, Vector2 p2) {
        float dx = p2.x - p1.x, dy = p2.y - p1.y;
        return Math.atan2(dy, dx);
    }

    public static Vector2 projectVector(Vector2 v, double angle, float length) {
        return v.cpy().add((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }
}
