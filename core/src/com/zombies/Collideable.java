package com.zombies;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface Collideable {

	public void handleCollision(Fixture f);
	
}
