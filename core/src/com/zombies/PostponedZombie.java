package com.zombies;

import com.badlogic.gdx.math.Vector2;
import com.zombies.map.building.Box;

public class PostponedZombie {

	private long createdAt = System.currentTimeMillis();
	private Box b;
	private Vector2 position;
	private Vector2 velocity;
	private long post;
	private GameView view;
	private Unit u;
	
	public PostponedZombie(GameView view, Box box, Vector2 position, Vector2 velocity, Unit u, long post) {
		this.view = view;
		b = box;
		this.position = position;
		this.velocity = velocity;
		this.post = post;
		this.u = u;
	}
	
	public void update() {
		if (System.currentTimeMillis() > createdAt + post) {
			Zombie z = new Zombie(view, b, position);
			b.addZombie(z);
			z.getBody().setLinearVelocity(velocity);
			view.dumpPostZombie(this);
			z.attack(u);
		}
	}
	
}
