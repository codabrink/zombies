package com.zombies.overlappable;

public interface Overlappable {
    public boolean overlaps(CircleOverlappable co);
    public boolean overlaps(PolygonOverlappable po);
    public boolean overlaps(Overlappable o);
}
