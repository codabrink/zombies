package com.zombies.map.building;

import com.badlogic.gdx.math.Vector2;
import com.zombies.util.Assets.MATERIAL;

public class FramelessDoorWall extends Wall {
    protected static final float widthFactor = 0.3f;
    protected Vector2 doorStart, doorEnd;
    protected Building building;

    public FramelessDoorWall(Vector2 p1, Vector2 p2, Building building, MATERIAL leftMaterial, MATERIAL rightMaterial) {
        super(p1, p2, leftMaterial, rightMaterial);

        this.building = building;

        float dst = p1.dst(p2);

        doorStart = p2.cpy().sub(p1).setAngleRad((float) angle).setLength(dst * 0.3f).add(p1);
        doorEnd   = p2.cpy().sub(p1).setAngleRad((float) angle).setLength(dst * 0.7f).add(p1);

        points.add(new WallPoint(p1.cpy(), 1));
        points.add(new WallPoint(doorStart, -0.2f));
        points.add(new WallPoint(doorEnd, 1));
        points.add(new WallPoint(p2, 0));
    }
}
