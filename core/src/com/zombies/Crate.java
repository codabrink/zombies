package com.zombies;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Crate implements Collideable  {
	
	private Body body;
	private PolygonShape shape;
	private BodyDef bDef = new BodyDef();
	private FixtureDef fDef = new FixtureDef();
	private Random random = new Random();
	private float height, width;
	private GameView view;
	private C c;

    private SpriteBatch batch;

	public Crate(GameView view, Vector2 position) {
		
		this.view = view;
		this.c = view.c;
		
		height = 2f;
		width = 2f;

        batch = new SpriteBatch();

		bDef.allowSleep = true;
		bDef.fixedRotation = false;
		bDef.bullet = false;
		bDef.position.set(position);
		bDef.angle = random.nextFloat() * 180;
		bDef.type = BodyType.DynamicBody;
		bDef.linearDamping = 4f;
		bDef.angularDamping = 2f;
		
		body = view.getWorld().createBody(bDef);
		shape = new PolygonShape();
		shape.setAsBox(width, height);
		MassData mass = new MassData();
		mass.mass = 6f;
		body.setMassData(mass);
		body.setUserData(new BodData("crate", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;

		body.createFixture(fDef);
		
		float boxHeight = 2f;
		
	}
	
	public void draw() {
        batch.begin();
        batch.draw(view.getMeshes().crateTexture, body.getPosition().x, body.getPosition().y);
        batch.end();
	}

	public Vector2 getPoint(int i) {
		switch (i) {
		case 1:
			return new Vector2(body.getPosition().x - c.PLAYER_SIZE * 2 + width, body.getPosition().y - c.PLAYER_SIZE * 2 + height);
		case 2:
			return new Vector2(body.getPosition().x + width * 2 + c.PLAYER_SIZE * 2 + width, body.getPosition().y - c.PLAYER_SIZE * 2 + height);
		case 3:
			return new Vector2(body.getPosition().x + width * 2 + c.PLAYER_SIZE * 2 + width, body.getPosition().y + height * 2 + c.PLAYER_SIZE * 2 + height);
		case 4:
			return new Vector2(body.getPosition().x - c.PLAYER_SIZE * 2 + width, body.getPosition().y + height * 2 + c.PLAYER_SIZE * 2 + height);
		}
		return null;
	}

    public void update() {}

	@Override
	public void handleCollision(Fixture f) {
		String type = ((BodData)f.getBody().getUserData()).getType();
		Object o = ((BodData)f.getBody().getUserData()).getObject();
		if (type == "zombie" || type == "survivor") {
			Unit u = (Unit)o;
			Vector2 position = u.getBody().getPosition();
			if (position.x < body.getPosition().x) {
				if (view.getPlayer().getBody().getPosition().y < position.y) {
					u.shove(-10, -height, c.CRATE_MPOS_DURATION);
					return;
				} else {
					u.shove(-10, height, c.CRATE_MPOS_DURATION);
					return;
				}
			}
			else if (position.x > body.getPosition().x + width * 2) {
				if (view.getPlayer().getBody().getPosition().y < position.y) {
					u.shove(10, -height, c.CRATE_MPOS_DURATION);
					return;
				} else {
					u.shove(10, height, c.CRATE_MPOS_DURATION);
					return;
				}
			}
			else if (position.y < body.getPosition().y) {
				if (view.getPlayer().getBody().getPosition().x < position.x) {
					u.shove(-width, -10, c.CRATE_MPOS_DURATION);
					return;
				} else {
					u.shove(width, -10, c.CRATE_MPOS_DURATION);
				}
			}
			else if (position.y > body.getPosition().y + height * 2) {
				if (view.getPlayer().getBody().getPosition().x < position.x) {
					u.shove(-width, 10, c.CRATE_MPOS_DURATION);
					return;
				} else {
					u.shove(width, 10, c.CRATE_MPOS_DURATION);
					return;
				}
			}
		}
	}
}
