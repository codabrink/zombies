package com.zombies.zombie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.zombies.GameView;

public class Carcass {
    private GameView view;
    private Vector2 location;
    private Color color = new Color(0.3f, 0, 0, 0.3f);

    public Carcass(GameView view, Vector2 location) {
        this.view = view;
        this.location = location;
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(location.x - 0.5f, location.y - 0.5f, 1, 1);
        shapeRenderer.end();
    }
}
