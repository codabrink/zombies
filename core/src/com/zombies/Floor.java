package com.zombies;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Floor {
	private Box box;
	private float size;
	private int updateInt;
	private Random random = new Random();
	private GameView view;
	private Texture t;

	public Floor(GameView view, Box box) {
		this.box = box;
		this.view = view;
		size = C.BOX_HEIGHT / 2.0f;
		this.updateInt = random.nextInt(C.UPDATE_LIGHTING_INTERVAL);

		if (random.nextBoolean()) {
			t = view.getMeshes().floor1Texture;
		} else {
			t = view.getMeshes().floor2Texture;
		}

	}

	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        spriteBatch.begin();
        spriteBatch.draw(t, box.getX(), box.getY(), C.BOX_HEIGHT, C.BOX_WIDTH, C.BOX_WIDTH, C.BOX_HEIGHT, 1, 1, 0, 0, 0, t.getWidth(), t.getHeight(), false, false);
        spriteBatch.end();
	}
}