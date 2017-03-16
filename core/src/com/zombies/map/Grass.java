package com.zombies.map;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.Zone;

public class Grass implements HasZone {
    private float width, height;
    private Vector2 position;
    private Zone zone;

    public Grass(Vector2 p, float w, float h) {
        position = p;
        width = w;
        height = h;

        zone = Zone.getZone(p);
        zone.modelables.get(Zone.MATERIAL.GRASS).add(new ModelMeCallback() {
            @Override
            public void buildModel(MeshPartBuilder builder, Vector2 center) {
                builder.rect(0, 0, -0.2f,
                        width, 0, -0.2f,
                        width, height, -0.2f,
                        0, height, -0.2f,
                        1, 1, 1);
            }
        });
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
