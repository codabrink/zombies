package com.zombies.util;

import com.badlogic.gdx.math.Vector2;

public class LineSegment {
    public Vector2 p1, p2;
    public float[] formula;
    public LineSegment(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        formula = G.line(p1, p2);
    }

    public Vector2 intersectionPoint(LineSegment ls) {
        return G.segmentIntersectionPoint(p1, p2, ls.p1, ls.p2, formula, ls.formula);
    }
}
