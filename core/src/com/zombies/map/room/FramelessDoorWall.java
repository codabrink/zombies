package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;
import com.zombies.util.Assets.MATERIAL;

public class FramelessDoorWall extends Wall {
    protected static final float widthFactor = 0.3f;
    protected Vector2 doorStart, doorEnd;
    protected Building building;

    public FramelessDoorWall(Vector2 p1, Vector2 p2, Building building, MATERIAL material) {
        super(p1, p2, material);

        this.building = building;

        float dx = Math.abs(p2.x - p1.x);
        float dy = Math.abs(p2.y - p1.y);

        doorStart = new Vector2(p1.x + dx * widthFactor, p1.y + dy * widthFactor);
        doorEnd   = new Vector2(p1.x + dx * (1 - widthFactor), p1.y + dy * (1 - widthFactor));

        points.add(new WallPoint(new Vector2(p1), 1));
        points.add(new WallPoint(doorStart, -0.2f));
        points.add(new WallPoint(doorEnd, 1));
        points.add(new WallPoint(p2, 0));
    }
}
