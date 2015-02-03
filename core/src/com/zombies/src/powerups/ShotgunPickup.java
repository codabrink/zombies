package com.zombies.src.powerups;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.zombies.src.guns.Shotgun;
import com.zombies.src.zombies.BodData;
import com.zombies.src.zombies.Box;
import com.zombies.src.zombies.C;
import com.zombies.src.zombies.Collideable;
import com.zombies.src.zombies.GameView;
import com.zombies.src.zombies.Powerup;
import com.zombies.src.zombies.Unit;

import java.util.Random;

public class ShotgunPickup extends Powerup implements Collideable {

	private BodyDef bDef = new BodyDef();
	private Body body;
	private C c;
	private FixtureDef fDef = new FixtureDef();
	private float radius = 0.8f;
	private CircleShape shape;
	private GameView view;
	private Box box;
	private Random random = new Random();
	
	public ShotgunPickup(GameView view, Box box) {
		super(view);
		this.c = view.c;
		this.view = view;
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
		shape.setRadius(c.PLAYER_SIZE * 0.75f);
		MassData mass = new MassData();
		mass.mass = .1f;
		body.setMassData(mass);
		body.setUserData(new BodData("shotgun_pickup", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;
		
		body.createFixture(fDef);
	}
	
	@Override
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        float radius = c.PLAYER_SIZE * 0.75f;
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
			u.addGun(new Shotgun(view, u, c.SHOTGUN_AMMO));
			view.getMeshes().shotgunPickup.play();
			this.destroy();
		}
	}
}