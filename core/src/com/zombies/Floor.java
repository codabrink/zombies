package com.zombies;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.util.Assets;

public class Floor {
    private Box box;
    private ModelInstance floorModelInstance;

    public Floor(Box box) {
        this.box = box;
        floorModelInstance = new ModelInstance(Assets.a.get("data/models/floor.g3dj", Model.class));
        floorModelInstance.transform.setToTranslation(box.getPosition().x + box.width / 2, box.getPosition().y + box.height / 2, 0);
        floorModelInstance.transform.rotate(Vector3.X, 90);
        floorModelInstance.transform.scale(Assets.FLOOR_SCALE, Assets.FLOOR_SCALE, Assets.FLOOR_SCALE);
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        modelBatch.begin(GameView.gv.getCamera());
        modelBatch.render(floorModelInstance, GameView.gv.environment);
        modelBatch.end();
    }
}
