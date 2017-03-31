package com.zombies.interfaces.Geom;

import com.badlogic.gdx.math.Vector2;

public interface Line {
    public float[] getFormula();
    public Vector2 intersectionPoint(Line l);
    public Vector2 intersectionPointInclusive(Line l);
    public boolean inRange(Vector2 point);
    public boolean inRangeInclusive(Vector2 point);
}
