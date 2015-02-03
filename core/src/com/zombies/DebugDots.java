package com.zombies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DebugDots {

    //TODO Old class, currently unused

    private GameView view;
    Vector2 p1;
    Vector2 p2;
    private float radius = 0.5f;

    public DebugDots(GameView view, Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 1, 1);
        shapeRenderer.rect(p1.x - radius, p1.y - radius, radius * 2, radius * 2);
        shapeRenderer.rect(p2.x - radius, p2.y - radius, radius * 2, radius * 2);
        shapeRenderer.end();
    }
}
