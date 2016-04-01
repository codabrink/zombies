package com.zombies;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
        spriteBatch.draw(t, box.x(), box.y(), C.BOX_HEIGHT, C.BOX_WIDTH, C.BOX_WIDTH, C.BOX_HEIGHT, 1, 1, 0, 0, 0, t.getWidth(), t.getHeight(), false, false);
        spriteBatch.end();
	}
}