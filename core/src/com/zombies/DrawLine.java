package com.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DrawLine {

	private Vector2 p1, p2;
    private Color color;
    GameView view;

	public DrawLine(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        System.out.println("New line at " + p1.x + "," + p1.y + "  " + p2.x + "," + p2.y);
        view = GameView.gv;
        color = Color.WHITE;
	}


	public void draw() {
        view.getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
        view.getShapeRenderer().setColor(color);
        view.getShapeRenderer().line(p1.x * view.scale, p1.y * view.scale, p2.x * view.scale, p2.y * view.scale);
        view.getShapeRenderer().end();
	}

	public void update() {}
    public void setColor(Color c) {
        color = c;
    }
}
