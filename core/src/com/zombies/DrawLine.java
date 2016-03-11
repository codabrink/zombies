package com.zombies;

import java.util.Random;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.util.MyVector2;
import com.util.Util;

public class DrawLine {

	private Vector2 p1, p2;
    GameView view;

	public DrawLine(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        view = GameView.gv;
	}
	
	public void draw() {
        view.getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
        view.getShapeRenderer().setColor(1, 1, 1, 1);
        view.getShapeRenderer().line(p1.x * view.scale, p1.y * view.scale, p2.x * view.scale, p2.y * view.scale);
        view.getShapeRenderer().end();
	}

	public void update() {}
}
