package com.zombies.abstract_classes;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.interfaces.HasZone;
import com.zombies.util.G;
import com.zombies.util.LineSegment;
import com.zombies.util.U;

public class Overlappable implements HasZone {
    public float         width, height;
    protected Vector2    position, center;
    protected Vector2[]    corners;
    public LineSegment[] lines;

    private Vector2 zonedPosition = null;
    private long zonedTimestamp = 0l;

    protected Zone zone;

    public Overlappable() {}

    public Overlappable(Vector2 position, float width, float height) {
        setCorners(new Vector2[]{
                position,
                position.cpy().add(width, 0),
                position.cpy().add(width, height),
                position.cpy().add(0, height)
        });
    }

    // corners need to be oriented in counter-clockwise fashion
    protected void setCorners(Vector2[] corners) {
        this.corners = corners;
        lines = new LineSegment[corners.length]; // line forumlas are cached for performance
        for (int i = 0; i < corners.length; i++)
            lines[i] = new LineSegment(corners[i], corners[(i + 1) % corners.length]);
    }
    public Vector2[] getCorners() { return corners; }


    public Vector2 getCenter() {
        if (center != null)
            return center;
        float x = 0, y = 0;
        for (Vector2 corner : corners) {
            x += corner.x;
            y += corner.y;
        }
        center = new Vector2(x / corners.length, y / corners.length);
        return center;
    }
    // TODO: currently only returns true if lines intersect
    public boolean overlaps(Overlappable o) {
        int closest      = closestCornerTo(o);
        int oClosest     = o.closestCornerTo(this);
        for (int i = -1; i <= 0; i++)
            for (int ii = -1; ii <= 0; ii++)
                if (lines[U.mod(closest + i, lines.length)].intersectionPoint(o.lines[U.mod(oClosest + ii, o.lines.length)]) != null)
                    return true;
        return false;
    }

    public boolean contains(float x, float y) {
        return G.rectContains(x, y, position, width, height);
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

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
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    public int closestCornerTo(Overlappable o) {
        int closestIndex = 0;
        float closestDistance = corners[0].dst(o.getCenter());
        for (int i = 1; i < corners.length; i++) {
            float distance = corners[i].dst(o.getCenter());
            if (distance > closestDistance)
                continue;

            closestDistance = distance;
            closestIndex    = i;
        }
        return closestIndex;
    }
}
