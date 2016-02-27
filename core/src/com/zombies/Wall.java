package com.zombies;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Wall implements Collideable {
	private float x1, y1, x2, y2;
	private Box box;
	private Body body;
	private ArrayList<EdgeShape> shapes;
	private ArrayList<DrawLine> lines;
	private GameView view;
	public boolean door = false;
	int index;
	
	public Wall(Box box, float x1, float y1, float x2, float y2, int index) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.box = box;
		this.view = GameView.m;
		this.index = index;
		
		//set up arraylists
		shapes = new ArrayList<EdgeShape>();
		lines = new ArrayList<DrawLine>();
		
		lines.add(new DrawLine(view, x1 + box.getX(), y1 + box.getY(), x2 + box.getX(), y2 + box.getY()));
		
		//set up physics
		body = view.getWorld().createBody(new BodyDef());
        EdgeShape shape = new EdgeShape();
		shape.set(new Vector2(x1, y1), new Vector2(x2, y2));
		shapes.add(shape);
		body.createFixture(shape, 0);
		body.setTransform(new Vector2(box.getX(), box.getY()), body.getAngle());
		body.setUserData(new BodData("wall", this));
	}
	
	public Zombie spawnZobie() {
		if (isDoor()) {
			Zombie z = new Zombie(view, box, doorPosition());
			box.addZombie(z);
			
			z.getBody().setLinearVelocity(getVector());
			return z;
		}
		return null;
	}
	
	public Vector2 getVector() {
		Vector2 vel = null;
		switch (index) {
		case 0:
			vel = new Vector2(0, 10);
			break;
		case 1:
			vel = new Vector2(-10, 0);
			break;
		case 2:
			vel = new Vector2(0, -10);
			break;
		case 3:
			vel = new Vector2(10, 0);
			break;
		}
		return vel;
	}
	
	public void draw() {
		for (DrawLine l: lines) {
			l.draw();
		}
	}
	
	public boolean isDoor() {
		return door;
	}
	
	public Body getBody() {return body;}
	
	public void makeDoor() {
		removeWall();
		EdgeShape s1 = new EdgeShape();
		EdgeShape s2 = new EdgeShape();
		DrawLine d1 = null, d2 = null;
		if (x1 == x2) {
			d1 = new DrawLine(view, x1 + box.getX(), y1 + box.getY(), x1 + box.getX(), y1 + C.BOX_HEIGHT / 2f - C.PLAYER_SIZE * C.DOOR_SIZE + box.getY());
			d2 = new DrawLine(view, x1 + box.getX(), y1 + C.BOX_HEIGHT / 2f + C.PLAYER_SIZE * C.DOOR_SIZE + box.getY(), x1 + box.getX(), y2 + box.getY());
		}
		else if (y1 == y2) {
			d1 = new DrawLine(view, x1 + box.getX(), y1 + box.getY(), x1 + C.BOX_WIDTH / 2f - C.PLAYER_SIZE * C.DOOR_SIZE + box.getX(), y1 + box.getY());
			d2 = new DrawLine(view, x1 + box.getX() + C.BOX_WIDTH / 2f + C.PLAYER_SIZE * C.DOOR_SIZE, y1 + box.getY(), x2 + box.getX(), y1 + box.getY());
		}
		lines.add(d1);
		lines.add(d2);
		s1.set(d1.getV1(box), d1.getV2(box));
		s2.set(d2.getV1(box), d2.getV2(box));
		body.createFixture(s1, 0);
		body.createFixture(s2, 0);
		
		door = true;
	}
	
	public Vector2 doorPosition() {
		if (isDoor()) {
			return new Vector2((x1 + x2) / 2.0f + box.getX(), (y1 + y2) / 2.0f + box.getY());
		} else {
			return null;
		}
	}
	
	public void removeWall() {
		view.getWorld().destroyBody(body);
		body = view.getWorld().createBody(new BodyDef());
		body.setTransform(new Vector2(box.getX(), box.getY()), body.getAngle());
		lines = new ArrayList<DrawLine>();
	}

	public void unload() {
		// unload the wall
	}

	public void update() {
		for (DrawLine d: lines) {
			d.update();
		}
	}

	@Override
	public void handleCollision(Fixture f) {
		// TODO Auto-generated method stub
		
	}
	
}
