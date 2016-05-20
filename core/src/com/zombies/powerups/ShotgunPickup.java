package com.zombies.powerups;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombies.BodData;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.interfaces.Collideable;
import com.zombies.Unit;
import com.zombies.guns.Shotgun;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;

public class ShotgunPickup extends Powerup implements Collideable {

	private BodyDef bDef = new BodyDef();
	private Body body;
	private FixtureDef fDef = new FixtureDef();
	private CircleShape shape;
	private Box box;
	private Random random = new Random();
	
	public ShotgunPickup(Box box) {
		super();
		this.box = box;
		shape = new CircleShape();
		
		bDef.allowSleep = true;
		bDef.fixedRotation = false;
		bDef.linearDamping = 0.9f;
		bDef.angularDamping = 3f;
		bDef.angle = random.nextFloat() * 360f;
		bDef.position.set(box.randomPoint());
		bDef.type = BodyType.DynamicBody;
		
		body = view.getWorld().createBody(bDef);
		shape.setRadius(C.PLAYER_SIZE * 0.75f);
		MassData mass = new MassData();
		mass.mass = .1f;
		body.setMassData(mass);
		body.setUserData(new BodData("shotgun_pickup", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;
		
		body.createFixture(fDef);
	}
	
	@Override
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        float radius = C.PLAYER_SIZE * 0.75f;
        spriteBatch.begin();
        spriteBatch.draw(view.getMeshes().shotgunTexture, body.getPosition().x - radius, body.getPosition().y - radius, radius, radius, radius * 4, radius * 2, 1, 1, body.getAngle(), 0, 0, 256, 64, false, false);
        spriteBatch.end();
	}
	
	private void destroy() {
		shape.dispose();
		body.getWorld().destroyBody(body);
		box.getPowerups().remove(this);
	}
	
	@Override
	public void update() {
		
	}

	@Override
	public void handleCollision(Fixture f) {
		String type = ((BodData)f.getBody().getUserData()).getType();
		Object o = ((BodData)f.getBody().getUserData()).getObject();
		if (type == "player") {
			Unit u = (Unit)o;
			u.addGun(new Shotgun(u, C.SHOTGUN_AMMO));
			view.getMeshes().shotgunPickup.play();
			this.destroy();
		}
	}
}