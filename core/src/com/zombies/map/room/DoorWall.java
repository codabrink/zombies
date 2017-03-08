package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;

public class DoorWall extends Wall {
    private static final float widthFactor = 0.3f;
    private DoorContainer doorContainer;

    public DoorWall(Vector2 p1, Vector2 p2, Building b) {
        super(p1, p2, b);

        float dx = Math.abs(p2.x - p1.x);
        float dy = Math.abs(p2.y - p1.y);

        Vector2 doorStart = new Vector2(p1.x + dx * widthFactor, p1.y + dy * widthFactor);
        Vector2 doorEnd   = new Vector2(p1.x + dx * (1 - widthFactor), p1.y + dy * (1 - widthFactor));

        points.add(new WallPoint(new Vector2(p1), 1));
        points.add(new WallPoint(doorStart, -0.2f));
        points.add(new WallPoint(doorEnd, 1));
        points.add(new WallPoint(p2, 0));

        doorContainer = new DoorContainer(doorStart, doorEnd, building);
    }

    @Override
    public void buildWallMesh(Vector2 center) {
        super.buildWallMesh(center);
        doorContainer.buildMesh(center);
    }
}
