package com.zombies;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Floor {
    private Box box;
    private Random random = new Random();
    private ModelInstance floorModelInstance;
    private Model floorModel;

    public Floor(Box box) {
        this.box = box;
        AssetManager assets = new AssetManager();
        assets.load("data/models/floor.g3db", Model.class);
        assets.finishLoading();
        floorModel = assets.get("data/models/floor.g3db", Model.class);
        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslation(box.getPosition().x, box.getPosition().y, 0);
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        GameView.gv.modelBatch.begin(GameView.gv.getCamera());
        GameView.gv.modelBatch.render(floorModelInstance, GameView.gv.environment);
        GameView.gv.modelBatch.end();
    }
}