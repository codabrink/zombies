package com.zombies.map.building.door;

import com.badlogic.gdx.math.Vector2;
import com.zombies.map.building.Building;
import com.zombies.map.building.Wall;
import com.zombies.map.building.WallPoint;
import com.zombies.util.Assets.MATERIAL;

public class FramelessDoorWall extends Wall {
    protected static final float widthFactor = 0.3f;
    protected Vector2 doorStart, doorEnd;
    protected Building building;

    public FramelessDoorWall(Vector2 p1, Vector2 p2, Building building, MATERIAL leftMaterial, MATERIAL rightMaterial) {
        super(p1, p2, leftMaterial, rightMaterial);

        this.building = building;

        float dst = p1.dst(p2),
                l1 = dst * 0.3f,
                l2 = dst * 0.4f;




        doorStart = p2.cpy().sub(p1).setAngleRad((float) angle).setLength(l1).add(p1);
        doorEnd   = p2.cpy().sub(p1).setAngleRad((float) angle).setLength(dst * 0.7f).add(p1);

        points.add(new WallPoint(p1.cpy(),  1,     l1));
        points.add(new WallPoint(doorStart, -0.2f, l2));
        points.add(new WallPoint(doorEnd,   1,     l1));
    }
}
