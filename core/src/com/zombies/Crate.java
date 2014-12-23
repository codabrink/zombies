package com.zombies;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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
	private Mesh squareMesh;
	private Mesh bottomMesh;
	private Mesh leftMesh;
	private Mesh rightMesh;
	private Mesh topMesh;
	private float[] verticies;
	private float[] bottom;
	private float[] left;
	private float[] right;
	private float[] top;
	
	public Crate(GameView view, Vector2 position) {
		
		this.view = view;
		this.c = view.c;
		
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
		
		squareMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		bottomMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		topMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		leftMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		rightMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		
				verticies = new float[] {
		                -width, -height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 0, 0,
		                width, -height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 1, 0,
		                -width, height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 0, 1,
		                width, height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 1, 1};
				bottom = new float[] {
						-width, -height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 0, 0,
		                width, -height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 1, 0,
		                -width, -height, 0, Color.toFloatBits(150, 150, 150, 255), 0, 1,
		                width, -height, 0, Color.toFloatBits(150, 150, 150, 255), 1, 1 };
				top = new float[] {
						-width, height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 0, 0,
		                width, height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 1, 0,
		                -width, height, 0, Color.toFloatBits(150, 150, 150, 255), 0, 1,
		                width, height, 0, Color.toFloatBits(150, 150, 150, 255), 1, 1 };
				left = new float[] {
						-width, height, 0, Color.toFloatBits(150, 150, 150, 255), 0, 0,
		                -width, height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 1, 0,
		                -width, -height, 0, Color.toFloatBits(150, 150, 150, 255), 0, 1,
		                -width, -height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 1, 1 };
				right = new float[] {
						width, height,  boxHeight, Color.toFloatBits(150, 150, 150, 255), 0, 0,
		                width, height,  0, Color.toFloatBits(150, 150, 150, 255), 1, 0,
		                width, -height, boxHeight, Color.toFloatBits(150, 150, 150, 255), 0, 1,
		                width, -height, 0, Color.toFloatBits(150, 150, 150, 255), 1, 1 };
 				squareMesh.setVertices(verticies);
		        squareMesh.setIndices(new short[] { 0, 1, 2, 3 });
		        bottomMesh.setVertices(bottom);
		        bottomMesh.setIndices(new short[] { 0, 1, 2, 3 });
		        topMesh.setVertices(top);
		        topMesh.setIndices(new short[] { 0, 1, 2, 3 });
		        leftMesh.setVertices(left);
		        leftMesh.setIndices(new short[] { 0, 1, 2, 3 });
		        rightMesh.setVertices(right);
		        rightMesh.setIndices(new short[] { 0, 1, 2, 3 });
	}
	
	public void draw() {
		Gdx.gl10.glPushMatrix();
		Gdx.gl10.glTranslatef(body.getPosition().x, body.getPosition().y, 0);
		Gdx.gl10.glRotatef((float)Math.toDegrees(body.getAngle()), 0, 0, 1);
		Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
		view.getMeshes().crateTexture.bind();
		leftMesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
		rightMesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
		bottomMesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
		topMesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
		squareMesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
		Gdx.graphics.getGL10().glDisable(GL10.GL_TEXTURE_2D);
		Gdx.gl10.glPopMatrix();
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
	
	public void update() {
		this.updateVerticies();
	}
	
	private void updateVerticies() {
		if (!c.UPDATE_LIGHTING || c.DISABLE_LIGHTING) { return; }
		float p1, d1;
		d1 = view.getPlayer().getBody().getPosition().dst(new Vector2(body.getPosition().x - width, body.getPosition().y - height));
		p1 = (c.LIGHT_DIST - d1) / c.LIGHT_DIST;
		if (p1 < 0f) { p1 = 0f; }
		
		float c1 = Color.toFloatBits((int)(255f * p1), (int)(255f * p1), (int)(255f * p1), 255);
		
		verticies[3] = c1;
		verticies[9] = c1;
		verticies[15] = c1;
		verticies[21] = c1;
		squareMesh.setVertices(verticies);
		
		bottom[3] = c1;
		bottom[9] = c1;
		bottom[15] = c1;
		bottom[21] = c1;
		bottomMesh.setVertices(bottom);

		top[3] = c1;
		top[9] = c1;
		top[15] = c1;
		top[21] = c1;
		topMesh.setVertices(top);

		left[3] = c1;
		left[9] = c1;
		left[15] = c1;
		left[21] = c1;
		leftMesh.setVertices(left);
	
		right[3] = c1;
		right[9] = c1;
		right[15] = c1;
		right[21] = c1;
		rightMesh.setVertices(right);
	
	}
	
}
