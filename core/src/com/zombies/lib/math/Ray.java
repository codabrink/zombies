package com.zombies.lib.math;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Geom.Line;
import com.zombies.lib.math.M;

public class Ray implements Line {
    public Vector2 p;
    private Vector2 p2;
    public float[] formula;

    public Ray(Vector2 p) {
        this.p = p;
        this.p2 = new Vector2(1, 0).add(p);
        formula = M.line(p, p2);
    }
    public Ray(Vector2 p, double angle) {
        this.p = p;
        this.p2 = new Vector2(1, 0).setAngleRad((float) angle).add(p);
        formula = M.line(p, p2);
    }

    @Override
    public Vector2 intersectionPoint(Line l) {
        return M.lineIntersectionPoint(this, l);
    }
    @Override
    public Vector2 intersectionPointInclusive(Line l) { return M.lineIntersectionPointInclusive(this, l); }
    @Override
    public boolean inRange(Vector2 point) {
        if ((M.inRange(point.x, p.x, p2.x) || Math.abs(p2.x - point.x) < Math.abs(p.x - point.x)) &&
                (M.inRange(point.y, p.y, p2.y) || Math.abs(p2.y - point.y) < Math.abs(p.y - point.y)))
            return true;
        return false;
    }

    @Override
    public boolean inRangeInclusive(Vector2 point) {
        if ((M.inRangeInclusive(point.x, p.x, p2.x) || Math.abs(p2.x - point.x) < Math.abs(p.x - point.x)) &&
                (M.inRangeInclusive(point.y, p.y, p2.y) || Math.abs(p2.y - point.y) < Math.abs(p.y - point.y)))
            return true;
        return false;
    }

    @Override
    public float[] getFormula() {
        return formula;
    }
}