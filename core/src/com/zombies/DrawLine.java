package com.zombies;

import java.util.Random;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DrawLine {

	private Vector2 v1, v2;
	private Mesh mesh;
	private C c;
	private float[] verticies;
	private GameView view;
	private Random random = new Random();
	private int updateInt;
	private int wallTexture = random.nextInt(3) + 1;
    private ShapeRenderer shapeRenderer;
    private float height, width;

	public DrawLine(GameView view, float x1, float y1, float x2, float y2) {
		v1 = new Vector2(x1, y1);
		v2 = new Vector2(x2, y2);
		this.c = view.c;
		this.view = view;
		updateInt = random.nextInt(c.UPDATE_LIGHTING_INTERVAL);

        height = y1 - y2;
        width = x1 - x2;

        shapeRenderer = view.getShapeRenderer();
	}
	
	public void draw() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.line(v1.x, v1.y, v2.x, v2.y);
        shapeRenderer.end();
	}
	
	public Vector2 getV1(Box box) {
		return new Vector2(v1.x - box.getX(), v1.y - box.getY());
	}
	
	public Vector2 getV2(Box box) {
		return new Vector2(v2.x - box.getX(), v2.y - box.getY());
	}
	
	public float getX1() {
		return v1.x;
	}
	
	public float getX2() {
		return v2.x;
	}
	
	public float getY1() {
		return v1.y;
	}
	
	public float getY2() {
		return v2.y;
	}

	public void update() {}
}
