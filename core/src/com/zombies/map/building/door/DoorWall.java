package com.zombies.map.building.door;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.map.building.Building;
import com.zombies.util.Assets.MATERIAL;

public class DoorWall extends FramelessDoorWall {
    private DoorContainer doorContainer;

    public DoorWall(Vector2 p1, Vector2 p2, Building building, MATERIAL leftMaterial, MATERIAL rightMaterial) {
        super(p1, p2, building, leftMaterial, rightMaterial);
        doorContainer = new DoorContainer(doorStart, doorEnd, building);
    }

    @Override
    public void buildRightMesh(MeshPartBuilder builder, Vector2 center) {
        super.buildRightMesh(builder, center);
        doorContainer.buildMesh(center);
    }
}
