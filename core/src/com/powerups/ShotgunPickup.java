package com.powerups;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zombies.BodData;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.Collideable;
import com.zombies.GameView;
import com.zombies.Powerup;
import com.zombies.Unit;
import com.guns.Shotgun;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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
	private C c;
	private FixtureDef fDef = new FixtureDef();
	private float radius = 0.8f;
	private CircleShape shape;
	private Mesh squareMesh;
	private float[] verticies;
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
		
		squareMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		
				verticies = new float[] {
		                -radius * 2, -radius, 0, 0, 0,
		                radius * 2, -radius, 0, 1, 0,
		                -radius * 2, radius, 0, 0, 1,
		                radius * 2, radius, 0, 1, 1};
				squareMesh.setVertices(verticies);
		        squareMesh.setIndices(new short[] { 0, 1, 2, 3 });
	}
	
	@Override
	public void draw() {
        float radius = c.PLAYER_SIZE * 0.75f;
        view.getWorldSpriteBatch().begin();
        view.getWorldSpriteBatch().draw(view.getMeshes().shotgunTexture, body.getPosition().x - radius, body.getPosition().y - radius, radius, radius, radius * 4, radius * 2, 1, 1, body.getAngle(), 0, 0, 256, 64, false, false);
        view.getWorldSpriteBatch().end();
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