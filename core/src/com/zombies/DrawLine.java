package com.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.interfaces.Drawable;

public class DrawLine {

	private Vector2 p1, p2;
    private Color color;
    GameView view;

	public DrawLine(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        view = GameView.gv;
        color = Color.WHITE;
	}

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.line(p1.x * view.scale, p1.y * view.scale, p2.x * view.scale, p2.y * view.scale);
        shapeRenderer.end();
	}

    public void setColor(Color c) {
        color = c;
    }
}
