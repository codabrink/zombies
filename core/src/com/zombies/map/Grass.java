package com.zombies.map;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.Zone;
import com.zombies.util.Assets.MATERIAL;

public class Grass implements HasZone {
    private float width, height;
    private Vector2 position;
    private Zone zone;
    private ModelMeCallback modelGroundCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            builder.rect(0, 0, -0.2f,
                    width, 0, -0.2f,
                    width, height, -0.2f,
                    0, height, -0.2f,
                    1, 1, 1);
        }
    };

    public Grass(Zone z, Vector2 p, float w, float h) {
        position = p;
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
