package com.zombies.util;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Geom.Line;

public class LineSegment implements Line {
    public Vector2 p1, p2;
    public float[] formula;
    public LineSegment(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        formula = G.line(p1, p2);
    }

    public Vector2 intersectionPoint(LineSegment ls) {
        return G.segmentIntersectionPoint(ls, this);
    }
    public Vector2 intersectionPoint(Ray r) {
        return G.segmentRayIntersectionPoint(this, r);
    }

    public boolean inRange(Vector2 point) {
        return G.inRangeInclusive(point.x, p1.x, p2.x) && G.inRangeInclusive(point.y, p1.y, p2.y);
    }

    @Override
    public float[] getFormula() {
        return formula;
    }
}
