package com.zombies.overlappable;

import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;

public class CircleOverlappable implements Overlappable {
    public Vector2 position;
    public float radius;

    public CircleOverlappable(Vector2 p, float r) {
        position = p;
        radius   = r;
    }

    @Override
    public boolean overlaps(CircleOverlappable co) {
        return position.dst(co.position) < radius + co.radius;
    }

    @Override
    public boolean overlaps(PolygonOverlappable po) {
        return po.overlaps(this);
    }

    @Override
    public boolean overlaps(Overlappable o) {
        if (o instanceof PolygonOverlappable)
            return overlaps((PolygonOverlappable) o);
        if (o instanceof CircleOverlappable)
            return overlaps((CircleOverlappable) o);
        throw new IllegalArgumentException("Overlappable checking not covered. Implement it.");
    }
}