package com.zombies.map.building.walkway;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.map.building.Building;
import com.zombies.map.building.BuildingGridable;

public class WalkwaySegment extends BuildingGridable {
    private ModelMeCallback modelFloorCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildFloorMesh(builder, center);
        }
    };

    public static WalkwaySegment createWalkwaySegment(Walkway walkway, int[] key) {
        Building building = walkway.getBuilding();
        if (building.gridMap.get(Building.stringify(key)) != null)
            return null;

        return new WalkwaySegment(walkway, key);
    }

    private WalkwaySegment(Walkway walkway, int[] key) {
        super(walkway.getBuilding(), key);
    }

    private void buildFloorMesh(MeshPartBuilder builder, Vector2 center) {

    }
}
