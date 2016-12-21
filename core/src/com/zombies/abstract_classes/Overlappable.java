package com.zombies.abstract_classes;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.IOverlappable;
import com.zombies.interfaces.Loadable;
import com.zombies.map.data.join.JoinOverlappableOverlappable;
import com.zombies.util.Geometry;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Overlappable implements IOverlappable, Loadable, HasZone {
    public float width, height;
    protected Vector2 position;
    protected ArrayList<Vector2> corners = new ArrayList<>();
    protected HashSet<JoinOverlappableOverlappable> joinOverlappableOverlappables = new HashSet<>();

    public ArrayList<Vector2> getCorners() { return corners; }
    public Vector2 getCenter() {
        return position.cpy().add(width / 2, height / 2);
    }
    public boolean overlaps(float x, float y, float w, float h) {
        return Geometry.rectOverlap(position.x, position.y, width, height, x, y, w, h);
    }
    public boolean contains(float x, float y) {
        return Geometry.rectContains(x, y, position, width, height);
    }
    public float edge(int direction) {
        switch(direction) {
            case 90:
                return position.y + height;
            case 0:
                return position.x + width;
            case 270:
                return position.y;
            case 180:
                return position.x;
        }
        return 0;
    }
    public float oppositeEdge(int direction) {
        return edge((direction + 180) % 360);
    }
    public Vector2 intersectPointOfLine(Vector2 p1, Vector2 p2) { return Geometry.edgeIntersection(p1, p2, this); }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public void addJoinOverlappableOverlappable(JoinOverlappableOverlappable joo) {
        for (JoinOverlappableOverlappable j : joinOverlappableOverlappables)
            if (j.o1 == this || j.o2 == this)
                return;
        joinOverlappableOverlappables.add(joo);
    }
}
