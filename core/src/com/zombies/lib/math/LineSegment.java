package com.zombies.lib.math;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Geom.Line;

public class LineSegment implements Line {
    public Vector2 a, b;
    public float[] formula;
    public LineSegment(Vector2 a, Vector2 b) {
        this.a = a;
        this.b = b;
        formula = M.line(a, b);
    }

    public boolean inRange(Vector2 point) {
        return (point.x == a.x && a.x == b.x || M.inRange(point.x, a.x, b.x)) &&
                (point.y == a.y && a.y == b.y || M.inRange(point.y, a.y, b.y));
    }

    @Override
    public boolean inRangeInclusive(Vector2 point) {
        return (point.x == a.x && a.x == b.x || M.inRangeInclusive(point.x, a.x, b.x)) &&
                (point.y == a.y && a.y == b.y || M.inRangeInclusive(point.y, a.y, b.y));
    }

    @Override
    public float[] getFormula() {
        return formula;
    }

    @Override
    public Vector2 intersectionPoint(Line l) {
        return M.lineIntersectionPoint(this, l);
    }

    @Override
    public Vector2 intersectionPointInclusive(Line l) {
        return  M.lineIntersectionPointInclusive(this, l);
    }
}
