package com.zombies.map;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.Zone;
import com.zombies.util.Assets.MATERIAL;

public class Grass implements HasZone {
    private float width, height;
    private Zone zone;
    private ModelMeCallback modelGroundCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            float hWidth = width / 2;
            float hHeight = height / 2;
            builder.rect(-hWidth, -hHeight, -0.2f,
                    hWidth, -hHeight, -0.2f,
                    hWidth, hHeight, -0.2f,
                    -hWidth, hHeight, -0.2f,
                    1, 1, 1);
        }
    };

    public Grass(Zone z, float w, float h) {
        width = w;
        height = h;

        z.addModelingCallback(MATERIAL.GRASS, modelGroundCallback);
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        zone = z;
    }
}
