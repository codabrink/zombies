package com.zombies.map.room;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.util.Assets.MATERIAL;

public class DoorWall extends FramelessDoorWall {
    private DoorContainer doorContainer;

    public DoorWall(Vector2 p1, Vector2 p2, Building building, MATERIAL material) {
        super(p1, p2, building, material);
        doorContainer = new DoorContainer(doorStart, doorEnd, building);
    }

    @Override
    public void buildWallMesh(MeshPartBuilder builder, Vector2 center) {
        super.buildWallMesh(builder, center);
        doorContainer.buildMesh(center);
    }
}
