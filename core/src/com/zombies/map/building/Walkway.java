package com.zombies.map.building;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.ModelMeCallback;

public class Walkway extends BuildingGridable {
    private ModelMeCallback modelFloorCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildFloorMesh(builder, center);
        }
    };

    public Walkway(Building building, int[] key) {
        super(building, key);
    }

    private void buildFloorMesh(MeshPartBuilder builder, Vector2 center) {

    }
}
