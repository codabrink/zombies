package com.zombies.src.powerups;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.zombies.src.zombies.Collideable;
import com.zombies.src.zombies.GameView;
import com.zombies.src.zombies.Powerup;

public class BulletSpeed extends Powerup implements Collideable {

	public BulletSpeed(GameView view) {
		super(view);
	}

	@Override
	public void update() {
		
	}
	
	@Override
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
		
	}
	
	@Override
	public void handleCollision(Fixture f) {
		// TODO Auto-generated method stub
		
	}

}
