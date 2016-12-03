package com.zombies.interfaces;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public interface Overlappable {
    // returns the bottom left corner with width and height
    // {x, y, w, h}
    public float width = 0f;
    public float height = 0f;
    public ArrayList<Vector2> corners = new ArrayList<>();

    public String className();
    public ArrayList<Vector2> getCorners();
    public Vector2 getCenter();
    public boolean overlaps(float x, float y, float w, float h);
    public boolean contains(float x, float y);
    public float edge(int direction);
    public float oppositeEdge(int direction);
    public Vector2 intersectPointOfLine(Vector2 p1, Vector2 p2);
}
