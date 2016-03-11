package com.zombies;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.util.MyVector2;
import com.util.Util;

public class Wall implements com.interfaces.Collideable {
	private Vector2 p1, p2;
    private MyVector2 p;
    private ArrayList<Float> holes = new ArrayList<Float>();

	private Box box;
	private Body body;
	private ArrayList<EdgeShape> shapes;
	private ArrayList<DrawLine> lines;
	private GameView view;
	public boolean door = false;
	int index;

    public Wall(Box box, float x, float y, float length, float angle) {
        view = GameView.gv;
        this.box = box;

        p = new MyVector2(x, y, length, angle);
        shapes = new ArrayList<EdgeShape>();
        lines = new ArrayList<DrawLine>();

        lines.add(new DrawLine(p.cpy().add(box.getPosition()), p.end().add(box.getPosition())));

        //set up physics
        body = view.getWorld().createBody(new BodyDef());
        EdgeShape shape = new EdgeShape();
        shape.set(p, p.end());
        shapes.add(shape);
        body.createFixture(shape, 0);
        body.setTransform(new Vector2(box.getX(), box.getY()), body.getAngle());
        body.setUserData(new BodData("wall", this));
    }

    public void makeDoor() {

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

	
	public Vector2 doorPosition() {
		if (isDoor()) {
			return new Vector2((p1.x+p2.x)/2, (p1.y+p2.y)/2);
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

    private void createHole(Vector2 holePosition, float holeSize) {

    }

    public Vector2 getP1() { return p; }
    public Vector2 getP2() { return p.end(); }

	@Override
	public void handleCollision(Fixture f) {
		createHole(f.getBody().getPosition(), 3f);
	}
	
}
