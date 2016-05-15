package com.zombies;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.util.Assets;

public class Floor {
    private Box box;
    private ModelInstance floorModelInstance;
    private float width, height;

    public Floor(float width, float height) {
        this.width = width;
        this.height = height;
        floorModelInstance = new ModelInstance(Assets.a.get("data/models/floor.g3dj", Model.class));
        floorModelInstance.transform.setToTranslation(box.getPosition().x + box.width / 2, box.getPosition().y + box.height / 2, 0);
        floorModelInstance.transform.rotate(Vector3.X, 90);
        floorModelInstance.transform.scale(Assets.FLOOR_SCALE, Assets.FLOOR_SCALE, Assets.FLOOR_SCALE);
    }

    public void buildMesh(ModelBuilder modelBuilder, Vector2 modelCenter) {
        Assets.meshBuilder.begin(Usage.Position | Usage.Normal, GL20.GL_TRIANGLES);
        Assets.meshBuilder.box(width, height, 0.1f);

    }
}
