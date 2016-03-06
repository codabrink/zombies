package com.powerups;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombies.BodData;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.Collideable;
import com.zombies.GameView;
import com.zombies.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class HealthPickup extends Powerup implements Collideable {

	private BodyDef bDef = new BodyDef();
	private Body body;
	private FixtureDef fDef = new FixtureDef();
	private float radius = 1f;
	private CircleShape shape;
	private Mesh squareMesh;
	private float[] verticies;
	private Box box;
	private Random random = new Random();
	private Sound heal = Gdx.audio.newSound(Gdx.files.internal("data/sound/heal.mp3"));
	
	public HealthPickup(Box box) {
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
		body.setUserData(new BodData("health_pickup", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;
		
		body.createFixture(fDef);
		
		squareMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		
				verticies = new float[] {
		                -radius, -radius, 0, 0, 0,
		                radius, -radius, 0, 1, 0,
		                -radius, radius, 0, 0, 1,
		                radius, radius, 0, 1, 1};
				squareMesh.setVertices(verticies);
		        squareMesh.setIndices(new short[] { 0, 1, 2, 3 });
	}

	@Override
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        //TODO: Draw health pickups.

        /*
		Gdx.gl10.glPushMatrix();
		Gdx.gl10.glTranslatef(body.getPosition().x, body.getPosition().y, 0);
		Gdx.gl10.glRotatef((float)Math.toDegrees(body.getAngle()), 0, 0, 1);
		Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
		Gdx.gl10.glEnable(GL10.GL_BLEND);
		Gdx.gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		view.getMeshes().healthTexture.bind();
		squareMesh.draw(GL10.GL_TRIANGLE_STRIP, 0, 4);
		Gdx.graphics.getGL10().glDisable(GL10.GL_TEXTURE_2D);
		Gdx.gl10.glPopMatrix();
		*/
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
			if (u.getHealth() < 100) {
				u.heal(30f);
				heal.play();
				this.destroy();
			}
		}
	}
	
}
