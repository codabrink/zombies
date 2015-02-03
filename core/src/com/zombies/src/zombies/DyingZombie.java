package com.zombies.src.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;

public class DyingZombie {
	
	private C c;
	private Vector2 position;
	private float span = 0;
	private long length = 500l;
	private long createdAt = System.currentTimeMillis();
	private float size;
	private Mesh squareMesh;
	private float[] verticies;
	private GameView view;
	
	public DyingZombie(GameView view, Vector2 position) {
		c = view.c;
		
		size = c.ZOMBIE_SIZE / 6f;
		this.position = position;
		this.view = view;
		
		squareMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		
				verticies = new float[] {
						-0.33f, -0.33f, 0, Color.toFloatBits(128, 0, 0, 255),
						0.33f, -0.33f, 0, Color.toFloatBits(192, 0, 0, 255),
						-0.33f, 0.33f, 0, Color.toFloatBits(192, 0, 0, 255),
						0.33f, 0.33f, 0, Color.toFloatBits(255, 0, 0, 255) };
				squareMesh.setVertices(verticies);
				squareMesh.setIndices(new short[] { 0, 1, 2, 3});
		
	}
	
	public void draw() {

	}
	
	public void update() {
		float percent = ((float)(System.currentTimeMillis() - createdAt)) / ((float)length);
		span = 2 * percent;
		if (System.currentTimeMillis() > createdAt + length) {
			view.dumpDyingZombie(this);
		}
	}
	
}
