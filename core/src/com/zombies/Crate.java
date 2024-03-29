package com.zombies;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.zombies.data.D;
import com.zombies.interfaces.Collideable;

public class Crate implements Collideable {
	
	private Body body;
	private PolygonShape shape;
	private BodyDef bDef = new BodyDef();
	private FixtureDef fDef = new FixtureDef();
	private Random random = new Random();
	private float height, width;
	private GameView view;

	public Crate(GameView view, Vector2 position) {
		this.view = view;
		
		height = 2f;
		width = 2f;

		bDef.allowSleep = true;
		bDef.fixedRotation = false;
		bDef.bullet = false;
		bDef.position.set(position);
		bDef.angle = random.nextFloat() * 180;
		bDef.type = BodyType.DynamicBody;
		bDef.linearDamping = 4f;
		bDef.angularDamping = 2f;
		
		body = D.world.createBody(bDef);
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
	
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        spriteBatch.begin();
        spriteBatch.draw(view.getMeshes().crateTexture, body.getPosition().x - width, body.getPosition().y - height, width, height, width * 2, height * 2, 1, 1, body.getAngle(), 0, 0, 32, 32, false, false);
        spriteBatch.end();
	}

	public Vector2 getPoint(int i) {
		switch (i) {
		case 1:
			return new Vector2(body.getPosition().x - C.PLAYER_SIZE * 2 + width, body.getPosition().y - C.PLAYER_SIZE * 2 + height);
		case 2:
			return new Vector2(body.getPosition().x + width * 2 + C.PLAYER_SIZE * 2 + width, body.getPosition().y - C.PLAYER_SIZE * 2 + height);
		case 3:
			return new Vector2(body.getPosition().x + width * 2 + C.PLAYER_SIZE * 2 + width, body.getPosition().y + height * 2 + C.PLAYER_SIZE * 2 + height);
		case 4:
			return new Vector2(body.getPosition().x - C.PLAYER_SIZE * 2 + width, body.getPosition().y + height * 2 + C.PLAYER_SIZE * 2 + height);
		}
		return null;
	}

	public void unload() {
		shape.dispose();
		body.setUserData(null);
		D.world.destroyBody(body);
		body = null;
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
					u.shove(-10, -height, C.CRATE_MPOS_DURATION);
					return;
				} else {
					u.shove(-10, height, C.CRATE_MPOS_DURATION);
					return;
				}
			}
			else if (position.x > body.getPosition().x + width * 2) {
				if (view.getPlayer().getBody().getPosition().y < position.y) {
					u.shove(10, -height, C.CRATE_MPOS_DURATION);
					return;
				} else {
					u.shove(10, height, C.CRATE_MPOS_DURATION);
					return;
				}
			}
			else if (position.y < body.getPosition().y) {
				if (view.getPlayer().getBody().getPosition().x < position.x) {
					u.shove(-width, -10, C.CRATE_MPOS_DURATION);
					return;
				} else {
					u.shove(width, -10, C.CRATE_MPOS_DURATION);
				}
			}
			else if (position.y > body.getPosition().y + height * 2) {
				if (view.getPlayer().getBody().getPosition().x < position.x) {
					u.shove(-width, 10, C.CRATE_MPOS_DURATION);
					return;
				} else {
					u.shove(width, 10, C.CRATE_MPOS_DURATION);
					return;
				}
			}
		}
	}
}
