package com.zombies.util;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Geom.Line;

public class Ray implements Line {
    public Vector2 p;
    private Vector2 p2;
    public float[] formula;

    public Ray(Vector2 p) {
        this.p = p;
        this.p2 = new Vector2(1, 0).add(p);
        formula = G.line(p, p2);
    }
    public Ray(Vector2 p, double angle) {
        this.p = p;
        this.p2 = new Vector2(1, 0).setAngleRad((float) angle).add(p);
        formula = G.line(p, p2);
    }

    @Override
    public Vector2 intersectionPoint(Line l) {
        return G.lineIntersectionPoint(this, l);
    }
    @Override
    public Vector2 intersectionPointInclusive(Line l) { return G.lineIntersectionPointInclusive(this, l); }
    @Override
    public boolean inRange(Vector2 point) {
        if ((G.inRange(point.x, p.x, p2.x) || Math.abs(p2.x - point.x) < Math.abs(p.x - point.x)) &&
                (G.inRange(point.y, p.y, p2.y) || Math.abs(p2.y - point.y) < Math.abs(p.y - point.y)))
            return true;
        return false;
    }

    @Override
    public boolean inRangeInclusive(Vector2 point) {
        if ((G.inRangeInclusive(point.x, p.x, p2.x) || Math.abs(p2.x - point.x) < Math.abs(p.x - point.x)) &&
                (G.inRangeInclusive(point.y, p.y, p2.y) || Math.abs(p2.y - point.y) < Math.abs(p.y - point.y)))
            return true;
        return false;
    }

    @Override
    public float[] getFormula() {
        return formula;
    }
}