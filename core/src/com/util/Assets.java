package com.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.zombies.C;

public class Assets {
    public static AssetManager a;
    public static ModelBuilder modelBuilder;
    public static MeshBuilder meshBuilder;
    public static float FLOOR_SCALE;

    public Assets() {
        a = new AssetManager();
        modelBuilder = new ModelBuilder();
        meshBuilder = new MeshBuilder();
        a.load("data/floor1.png", Texture.class);
        a.load("data/models/floor.g3dj", Model.class);
        a.finishLoading();

        calculateScales();
    }

    private void calculateScales() {
        ModelInstance floorModelInstance = new ModelInstance(a.get("data/models/floor.g3dj", Model.class));
        floorModelInstance.transform.rotate(Vector3.X, 90);
        BoundingBox floorModelBoundingBox = new BoundingBox();
        floorModelInstance.calculateBoundingBox(floorModelBoundingBox);

        FLOOR_SCALE = C.BOX_SIZE / floorModelBoundingBox.getWidth();
    }
}
