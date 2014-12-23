package com.zombies;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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

	public DrawLine(GameView view, float x1, float y1, float x2, float y2) {
		v1 = new Vector2(x1, y1);
		v2 = new Vector2(x2, y2);
		this.c = view.c;
		this.view = view;
		updateInt = random.nextInt(c.UPDATE_LIGHTING_INTERVAL);
		
		mesh = new Mesh(true, 4, 4, 
                new VertexAttribute(Usage.Position, 3, "a_position"),
                new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		verticies = new float[] {
                v1.x, v1.y, 0, Color.toFloatBits(125, 125, 125, 255), 0, 0,
                v1.x, v1.y, 5, Color.toFloatBits(50, 50, 50, 255), 0, 1,
                v2.x, v2.y, 0, Color.toFloatBits(125, 125, 125, 255), 0.5f, 0,
                v2.x, v2.y, 5, Color.toFloatBits(50, 50, 50, 255), 0.5f, 1 };

		mesh.setVertices(verticies);
        mesh.setIndices(new short[] { 0, 1, 2, 3});
        
        
	}
	
	public void draw() {
		this.handleColors();
		Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
		switch(wallTexture) {
		case 1:
			view.getMeshes().wall1Texture.bind();
			break;
		case 2:
			view.getMeshes().wall2Texture.bind();
			break;
		default:
			view.getMeshes().wall2Texture.bind();
			break;
		}
		mesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
		Gdx.graphics.getGL10().glDisable(GL10.GL_TEXTURE_2D);
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
	
	private void handleColors() {
		if (c.DISABLE_LIGHTING) { return; }
		float d1 = v1.dst(view.getPlayer().getBody().getPosition());
		float d2 = v2.dst(view.getPlayer().getBody().getPosition());
		float p1 = (c.LIGHT_DIST - d1) / c.LIGHT_DIST;
		float p2 = (c.LIGHT_DIST - d2) / c.LIGHT_DIST;
		if (p1 < 0f) {
			p1 = 0f;
		}
		if (p2 < 0f) {
			p2 = 0f;
		}
		float top = 100f;
		float bottom = 220f;
		verticies[3] = Color.toFloatBits((int)(bottom * p1), (int)(bottom * p1), (int)(bottom * p1), 255);
		verticies[9] = Color.toFloatBits((int)(top * p1), (int)(top * p1), (int)(top * p1), 255);
		verticies[15] = Color.toFloatBits((int)(bottom * p2), (int)(bottom * p2), (int)(bottom * p2), 255);
		verticies[21] = Color.toFloatBits((int)(top * p2), (int)(top * p2), (int)(top * p2), 255);
		mesh.setVertices(verticies);
	}
	
	public void update() {
		handleColors();
	}
}
