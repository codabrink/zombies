package com.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.interfaces.Drawable;

/**
 * Created by Farrah on 4/28/2016.
 */
public class DebugLine implements Drawable {

    private Color color;
    private GameView view;
    private Vector2 v1;
    private Vector2 v2;

    public DebugLine(Vector2 v1, Vector2 v2) {
        this.color = Color.WHITE;
        this.view = GameView.gv;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public String className() {
        return "DebugLine";
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        view.getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
        view.getShapeRenderer().setColor(color);
        view.getShapeRenderer().line(v1.x, v1.y, v2.x, v2.y);
        view.getShapeRenderer().end();
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
