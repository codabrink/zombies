package com.powerups;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;

import com.interfaces.Collideable;

public class BulletSpeed extends Powerup implements Collideable {

	public BulletSpeed() {
		super();
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
