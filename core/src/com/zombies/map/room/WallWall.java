package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;
import com.zombies.util.Assets.MATERIAL;

public class WallWall extends Wall {
    public WallWall(Vector2 p1, Vector2 p2, MATERIAL leftMaterial, MATERIAL rightMaterial) {
        super(p1, p2, leftMaterial, rightMaterial);

        points.add(new WallPoint(p1, 1));
        points.add(new WallPoint(p2, 0));
    }
}
