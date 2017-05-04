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

    public WalkwaySegment(Building building, int[] key) {
        super(building, key);
    }

    private void buildFloorMesh(MeshPartBuilder builder, Vector2 center) {

    }
}
