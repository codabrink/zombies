package com.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DebugDots {

    //TODO Old class, currently unused

    private GameView view;
    Vector2 p1;
    Vector2 p2;
    private float radius = 0.5f * C.SCALE;
    private Color color;

    public DebugDots(Vector2 p1) {
        this.p1 = p1;
        color = new Color(1, 0, 1, 1);
    }
    public DebugDots(Vector2 p1, Color c) {
        this.p1 = p1;
        color = c;
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(p1.x - radius, p1.y - radius, radius * 2, radius * 2);
        shapeRenderer.end();
    }
}
