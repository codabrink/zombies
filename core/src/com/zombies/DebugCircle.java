package com.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Drawable;

public class DebugCircle implements Drawable {

    private Color color;
    private GameView view;
    private Vector2 p1;
    private Float r;
    private Zone z;

    public DebugCircle(Vector2 p1, Float r) {
        this.color = Color.ORANGE;
        this.view = GameView.gv;
        this.p1 = p1;
        this.r = r;
        this.z = Zone.getZone(p1);
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        view.getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
        view.getShapeRenderer().setColor(color);
        view.getShapeRenderer().circle(p1.x, p1.y, r, 20);
        view.getShapeRenderer().end();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Zone getZone() {
        return z;
    }

    @Override
    public void setZone(Zone z) {

    }
}
