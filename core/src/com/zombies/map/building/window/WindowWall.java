package com.zombies.map.building.window;

import com.badlogic.gdx.math.Vector2;
import com.zombies.map.building.Building;
import com.zombies.map.building.Wall;
import com.zombies.map.building.WallPoint;
import com.zombies.lib.Assets.MATERIAL;

public class WindowWall extends Wall {
    protected Vector2 windowStart, windowEnd;

    public WindowWall(Vector2 p1, Vector2 p2, Building b, MATERIAL leftMaterial, MATERIAL rightMaterial) {
        super(p1, p2, leftMaterial, rightMaterial);

        float dst = p1.dst(p2),
                l1 = dst * 0.25f,
                l2 = dst * 0.5f;

        windowStart = p2.cpy().sub(p1).setAngleRad((float) angle).setLength(l1).add(p1);
        windowEnd   = p2.cpy().sub(p1).setAngleRad((float) angle).setLength(dst * 0.75f).add(p1);

        points.add(new WallPoint(p1.cpy(),    1,     l1));
        points.add(new WallPoint(windowStart, -0.3f, l2));
        points.add(new WallPoint(windowStart, 0.3f,  l2));
        points.add(new WallPoint(windowEnd,   1,     l1));
    }
}
