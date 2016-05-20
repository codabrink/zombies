package com.zombies.interfaces;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by coda on 4/2/16.
 */
public interface Overlappable {
    // returns the bottom left corner with width and height
    // {x, y, w, h}
    public String className();
    public boolean overlaps(float x, float y, float w, float h);
    public float edge(char direction);
    public float oppositeEdge(char direction);
    public Vector2 intersectPointOfLine(Vector2 p1, Vector2 p2);
}
