package com.zombies.lib.math;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Geom.Line;
import com.zombies.lib.math.M;

public class LineSegment implements Line {
    public Vector2 p1, p2;
    public float[] formula;
    public LineSegment(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        formula = M.line(p1, p2);
    }

    public boolean inRange(Vector2 point) {
        return (point.x == p1.x && p1.x == p2.x || M.inRange(point.x, p1.x, p2.x)) &&
                (point.y == p1.y && p1.y == p2.y || M.inRange(point.y, p1.y, p2.y));
    }

    @Override
    public boolean inRangeInclusive(Vector2 point) {
        return (point.x == p1.x && p1.x == p2.x || M.inRangeInclusive(point.x, p1.x, p2.x)) &&
                (point.y == p1.y && p1.y == p2.y || M.inRangeInclusive(point.y, p1.y, p2.y));
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
