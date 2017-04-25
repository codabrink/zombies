package com.zombies.map.building;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.util.Assets;

public class WallTop {
    private Vector2 c1, c2, c3, c4;
    private float height;

    private ModelMeCallback modelMeCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildMesh(builder, center);
        }
    };

    public WallTop(Vector2 c1, Vector2 c2, Vector2 c3, Vector2 c4, float height) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
        this.height = height;

        Zone.getZone(c1).addModelingCallback(Assets.MATERIAL.GRAY, modelMeCallback);
    }

    public void buildMesh(MeshPartBuilder builder, Vector2 center) {
        builder.rect(
                c1.x - center.x, c1.y - center.y, height,
                c2.x - center.x, c2.y - center.y, height,
                c3.x - center.x, c3.y - center.y, height,
                c4.x - center.x, c4.y - center.y, height,
                1, 1, 1);
    }
}
