package com.zombies.map.building.window;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.map.building.Building;
import com.zombies.util.Assets.MATERIAL;
import com.zombies.util.G;

public class WindowFrame {
    private Vector2 p1, p2;
    private Building building;
    private float angle;
    private ModelMeCallback modelFrameCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildMesh(builder, center);
        }
    };

    public WindowFrame(Vector2 p1, Vector2 p2, Building b) {
        this.p1  = p1;
        this.p2  = p2;
        building = b;
        angle = (float) G.getAngle(p1, p2);

        Zone.getZone(p1).addModelingCallback(MATERIAL.FLOOR_WOOD, modelFrameCallback);
    }

    private void buildMesh(MeshPartBuilder builder, Vector2 center) {

    }
}
