package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Bullet implements Collideable {
	private Body body;
	private Shape shape;
	private Unit unit;
	private BodyDef bDef = new BodyDef();
	private FixtureDef fDef = new FixtureDef();
	private long createTime = System.currentTimeMillis();
	private boolean lethal = true;
	private C c;
	private Fixture f;
	private Vector2 direction;
	private Mesh squareMesh;
	private float[] verticies;
	private GameView view;
    private ShapeRenderer shapeRenderer;
	
	public Bullet(GameView view, Unit unit, short group, Vector2 position, Vector2 direction) {
		c = view.c;
		this.direction = direction;
		this.view = view;

        shapeRenderer = new ShapeRenderer();

		bDef.allowSleep = false;
		bDef.fixedRotation = true;
		bDef.bullet = true;
		bDef.position.set(position.add(scale(direction, c.PLAYER_SIZE * 1.1f)));
		bDef.type = BodyType.DynamicBody;
		bDef.linearVelocity.set(scale(direction, c.BULLET_SPEED));
		
		body = view.getWorld().createBody(bDef);
		shape = new CircleShape();
		shape.setRadius(0.1f);
		MassData mass = new MassData();
		mass.mass = 0.01f;
		body.setMassData(mass);
		body.setUserData(new BodData("bullet", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;
		fDef.filter.groupIndex = group;

		f = body.createFixture(fDef);
		
		this.unit = unit;
		
		squareMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		
				verticies = new float[] {
		                -c.BULLET_RADIUS, -c.BULLET_RADIUS, c.ZOOM_LEVEL, Color.toFloatBits(255, 255, 255, 255),
		                c.BULLET_RADIUS, -c.BULLET_RADIUS, c.ZOOM_LEVEL, Color.toFloatBits(255, 255, 255, 255),
		                -c.BULLET_RADIUS, c.BULLET_RADIUS, c.ZOOM_LEVEL, Color.toFloatBits(255, 255, 255, 255),
		                c.BULLET_RADIUS, c.BULLET_RADIUS, c.ZOOM_LEVEL, Color.toFloatBits(255, 255, 255, 255) };
				squareMesh.setVertices(verticies);   
		        squareMesh.setIndices(new short[] { 0, 1, 2, 3});
	}
	
	public void draw() {
		if (lethal = true && System.currentTimeMillis() < createTime + c.BULLET_LIFE) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, 1);
            shapeRenderer.rect(body.getPosition().x - c.BULLET_RADIUS, body.getPosition().y - c.BULLET_RADIUS, c.BULLET_RADIUS * 2, c.BULLET_RADIUS * 2);
            shapeRenderer.end();
		}
	}
	
	public Vector2 scale(Vector2 v, float len) {
		float realLen = (float) Math.sqrt(v.x * v.x + v.y * v.y);
		float scale = len / realLen;
		return new Vector2(v.x * scale, v.y * scale);
	}
	
	public Bullet refresh() {
		createTime = System.currentTimeMillis();
		lethal = true;
		return this;
	}
	
	public boolean isLethal() {
		return lethal;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public void reShoot(Vector2 position, Vector2 direction) {
		body.setTransform(position.add(scale(direction, c.PLAYER_SIZE * 1.1f)), body.getAngle());
		body.setLinearVelocity(scale(direction, c.BULLET_SPEED));
		lethal = true;
	}
	
	@Override
	public void handleCollision(Fixture f) {
		String type = ((BodData)f.getBody().getUserData()).getType();
		Object o = ((BodData)f.getBody().getUserData()).getObject();
		if (type == "zombie") {
			if (lethal)
				((Zombie)o).hitByBullet(unit);
		}
		lethal = false;
	}
	
	public void setPosition(Vector2 position) {
		body.setTransform(position, body.getAngle());
	}
	
	public void setVelocity(Vector2 velocity) {
		body.setLinearVelocity(velocity);
	}

	public void update() {
	}
	
}
