package com.zombies.overlappable;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.lib.math.LineSegment;
import com.zombies.lib.math.M;
import com.zombies.lib.math.Ray;
import com.zombies.lib.U;

import java.util.HashSet;

public class PolygonOverlappable implements Overlappable {
    public Vector2    position, center;
    public Vector2[]  corners;
    public LineSegment[] lines;

    public PolygonOverlappable() {}
    public PolygonOverlappable(Vector2[] corners) {
        setCorners(corners);
    }
    public PolygonOverlappable(Vector2 position, float width, float height) {
        setCorners(new Vector2[]{
                position,
                position.cpy().add(width, 0),
                position.cpy().add(width, height),
                position.cpy().add(0, height)
        });
    }

    // corners need to be oriented in counter-clockwise fashion
    protected void setCorners(Vector2[] corners) {
        setCorners(corners, true);
    }
    protected void setCorners(Vector2[] corners, boolean close) {
        this.corners = corners;
        lines = new LineSegment[(close ? corners.length : corners.length - 1)];
        for (int i = 0; i < lines.length; i++)
            lines[i] = new LineSegment(corners[i], corners[(i + 1) % corners.length]);
    }
    public Vector2[] getCorners() { return corners; }


    public Vector2 getCenter() {
        if (center != null)
            return center;
        float x = 0, y = 0;
        for (Vector2 corner : corners) {
            x += corner.x;
            y += corner.y;
        }
        center = new Vector2(x / corners.length, y / corners.length);
        return center;
    }

    @Override
    public boolean overlaps(CircleOverlappable co) {
        for (int i = 0; i <= corners.length; i++) {
            if (M.distanceOfPointFromLine(corners[i], corners[i+1 % corners.length], co.position) < co.radius)
                return true;
        }
        return false;
    }

    // TODO: currently only returns true if lines intersect
    @Override
    public boolean overlaps(PolygonOverlappable o) {
        int closest      = closestCornerTo(o);
        int oClosest     = o.closestCornerTo(this);
        for (int i = -1; i <= 0; i++)
            for (int ii = -1; ii <= 0; ii++)
                if (lines[U.mod(closest + i, lines.length)].intersectionPoint(o.lines[U.mod(oClosest + ii, o.lines.length)]) != null)
                    return true;
        return false;
    }

    @Override
    public boolean overlaps(Overlappable o) {
        if (o instanceof PolygonOverlappable)
            return overlaps((PolygonOverlappable) o);
        if (o instanceof CircleOverlappable)
            return overlaps((CircleOverlappable) o);
        throw new IllegalArgumentException("Overlappable checking not covered. Implement it.");
    }

    public int closestCornerTo(PolygonOverlappable o) {
        int closestIndex = 0;
        float closestDistance = corners[0].dst(o.getCenter());
        for (int i = 1; i < corners.length; i++) {
            float distance = corners[i].dst(o.getCenter());
            if (distance > closestDistance)
                continue;

            closestDistance = distance;
            closestIndex    = i;
        }
        return closestIndex;
    }

    public boolean contains(float x, float y) {
        return contains(new Vector2(x, y));
    }
    public boolean contains(Vector2 p) {
        Ray r = new Ray(p);
        int count = 0;
        for (LineSegment line : lines) {
            if (line.intersectionPointInclusive(r) != null)
                count++;
        }
        return !((count & 1) == 0);
    }

    public Vector2[] cropLine(Vector2 p1, Vector2 p2) {
        Vector2[] result = new Vector2[2];
        return null;
    }

    public Vector2 lineIntersect(Vector2 p1, Vector2 p2) {
        return lineIntersect(new LineSegment(p1, p2));
    }
    public Vector2 lineIntersect(LineSegment lineSegment) {
        Vector2 result = null;
        float intersectionDst = 0;
        for (LineSegment ls : lines) {
            Vector2 intersection = ls.intersectionPointInclusive(lineSegment);
            if (intersection == null) continue;
            if (!(result == null || intersection.dst(lineSegment.p1) < intersectionDst)) continue;

            intersectionDst = intersection.dst(lineSegment.p1);
            result          = intersection;
        }
        return result;

    }

}
