package com.zombies.interfaces;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by coda on 4/2/16.
 */
public interface Overlappable {
    // returns the bottom left corner with width and height
    // {x, y, w, h}
    public float width = 0f;
    public float height = 0f;

    public String className();
    public ArrayList<Vector2> getCorners();
    public Vector2 getCenter();
    public boolean overlaps(float x, float y, float w, float h);
    public boolean contains(float x, float y);
    public float edge(int direction);
    public float oppositeEdge(int direction);
    public Vector2 intersectPointOfLine(Vector2 p1, Vector2 p2);
}
