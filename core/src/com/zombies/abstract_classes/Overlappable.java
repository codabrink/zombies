package com.zombies.abstract_classes;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.IOverlappable;
import com.zombies.interfaces.Loadable;
import com.zombies.map.data.join.JoinOverlappableOverlappable;
import com.zombies.util.Geometry;

import java.util.HashSet;

public abstract class Overlappable implements IOverlappable, Loadable, HasZone {
    public float width, height;
    protected Vector2 position, center;
    protected Vector2[] corners = new Vector2[4];
    protected HashSet<JoinOverlappableOverlappable> joinOverlappableOverlappables = new HashSet<>();

    private Vector2 zonedPosition = null;
    private long zonedTimestamp = 0l;

    protected Zone zone;

    public Vector2[] getCorners() { return corners; }
    public Vector2 getCenter() {
        if (center != null)
            return center;
        center = new Vector2();
        for (Vector2 corner : corners)
            center.add(corner);
        return center.scl(1 / corners.length);
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

    public void processZoning() {
        if (C.DEBUG)
            checkForOversizing();

        for (int i = 0; i < corners.length; i++)
            for (Zone z : Zone.zonesOnLine(corners[i], corners[(i+1)%corners.length]))
                z.addObject(this);
    }

    private void checkForOversizing() {
        float max = 0;
        for (int i = 1; i < corners.length; i++)
            max = Math.max(max, corners[i - 1].dst(corners[i]));
        if (max > C.ZONE_SIZE * (C.DRAW_DISTANCE + 1))
            System.out.println("Overlappable: ERROR! Object is too large to render properly.");
    }

    @Override
    public void setZone(Zone z) {
        this.zone = Zone.getZone(getCenter());

        if (zonedPosition == position || System.currentTimeMillis() < zonedTimestamp + 1000l)
            return;

        zonedPosition = position;
        zonedTimestamp = System.currentTimeMillis();

        for (Vector2 corner : corners)
            Zone.getZone(corner).addObject(this);
    }

    @Override
    public Zone getZone() {
        return zone;
    }
}
