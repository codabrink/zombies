package com.powerups;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombies.GameView;

public class Powerup {
	protected GameView view;
	
	public Powerup(GameView view) {
		this.view = view;
	}
	
	public void update() {}
	
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {}
}
