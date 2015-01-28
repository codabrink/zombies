package com.zombies;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Zombie extends Unit implements Collideable{
	
	private C c;
	private long lastAttack = System.currentTimeMillis();
	private Player player;
	private Random random = new Random();
	private int updateInt;

	public Zombie(GameView view, Box box, Vector2 position) {
		super(view);
		c = view.c;
		this.box = box;
		player = view.getPlayer();
		
		updateInt = random.nextInt(c.UPDATE_LIGHTING_INTERVAL);
		
		speed = c.ZOMBIE_SPEED;
		
		bDef.allowSleep = true;
		bDef.fixedRotation = true;
		bDef.linearDamping = c.LINEAR_DAMPING;
		bDef.position.set(position);
		bDef.type = BodyType.DynamicBody;
		
		body = view.getWorld().createBody(bDef);
		shape.setRadius(c.ZOMBIE_SIZE * 0.75f);
		MassData mass = new MassData();
		mass.mass = .1f;
		body.setMassData(mass);
		body.setUserData(new BodData("zombie", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;
		
		body.createFixture(fDef);
		
		health = c.ZOMBIE_HEALTH;

		mPos = this.randomClosePoint();
		
		squareMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		
				verticies = new float[] {
						-0.5f, -0.5f, 0, Color.toFloatBits(128, 0, 0, 255),
						0.5f, -0.5f, 0, Color.toFloatBits(192, 0, 0, 255),
						-0.5f, 0.5f, 0, Color.toFloatBits(192, 0, 0, 255),
						0.5f, 0.5f, 0, Color.toFloatBits(255, 0, 0, 255) };
				squareMesh.setVertices(verticies);   
				squareMesh.setIndices(new short[] { 0, 1, 2, 3});
		
	}
	
	private void attack() {
		mPos = attack.getBody().getPosition();
		if (System.currentTimeMillis() < lastAttack + c.ZOMBIE_ATTACK_RATE) { return; }
		if (body.getPosition().dst(attack.getBody().getPosition()) < c.ZOMBIE_SIZE * 2) {
			attack.hurt(c.ZOMBIE_STRENGTH, this);
//			attack.getBody().applyForce(scale(attack.getBody().getPosition().sub(body.getPosition()), 100f), new Vector2());
			lastAttack = System.currentTimeMillis();
		}
	}
	
	public void attack(Unit u) {
		attack = u;
	}

    @Override
	public void draw() {
        view.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        view.getShapeRenderer().setColor(0, 1, 0, 1);
        view.getShapeRenderer().rect(body.getPosition().x - 0.5f, body.getPosition().y - 0.5f, 1, 1);
        view.getShapeRenderer().end();
	}
	
	@Override
	public void handleCollision(Fixture f) {
		String type = ((BodData)f.getBody().getUserData()).getType();
		Object o = ((BodData)f.getBody().getUserData()).getObject();
	}
	
	public void hitByBullet(Unit u) {
		this.hurt(c.BULLET_DAMAGE_FACTOR, u);
		this.updateColor();
	}
	
	@Override
	public void kill(Unit u) {
		view.addDyingZombie(new DyingZombie(view, body.getPosition()));
		destroy();
		box.getUnits().remove(this);
		if (u == view.getPlayer()) {
			view.getPlayer().addZombieKill();
		}
		dead = true;
		view.s.zombieKills ++;
		view.s.score += c.SCORE_ZOMBIE_KILL;
	}
	
	private Vector2 randomClosePoint() {
		float nx = body.getPosition().x + random.nextFloat() * 20 - 10;
		float ny = body.getPosition().y + random.nextFloat() * 20 - 10;
		return new Vector2(nx, ny);
	}

	@Override
	public void update() {
		//handle sleeping
		if (attack == null) {
			if (random.nextFloat() <= c.ZOMBIE_AWARENESS && this.isVisionClear()) {
				if (random.nextBoolean() == true) {
					attack = player;
				} else {
					attack = player.randomSurvivor();
				}
			}
		}
		
		if (this.canChangeMPos()) {
			if (attack != null) {
				//move zombie
				this.attack();
			} else {
				mPos = this.randomClosePoint();
			}
		}
		this.move();
		updateVerticies();
	}
	
	@Override
	public void move() {
        body.applyForce(mPos.sub(body.getPosition()).scl(c.ZOMBIE_AGILITY), new Vector2(), true);
		if (body.getLinearVelocity().len() > c.ZOMBIE_SPEED) { //Zombie is going too fast
            body.setLinearVelocity(body.getLinearVelocity().scl(c.ZOMBIE_SPEED));
		}
	}
	
	private void updateVerticies() {
		if (updateInt != view.getLightingCount() || c.DISABLE_LIGHTING) { return; }
		//calculate color
		float percent = health / c.ZOMBIE_HEALTH;
		if (percent < 0f) {
			percent = 0f;
		}
		float invPercent = 1.0f - percent;
		//calculate light
		float d = body.getPosition().dst(view.getPlayer().getBody().getPosition());
		float lp = (c.LIGHT_DIST - d) / c.LIGHT_DIST;
		if (lp < 0f) {
			lp = 0f;
		}
		verticies[3] = Color.toFloatBits((int)(128f * percent * lp), 0, (int)(128f * invPercent * lp), 255);
		verticies[7] = Color.toFloatBits((int)(192f * percent * lp), 0, (int)(192f * invPercent * lp), 255);
		verticies[11] = Color.toFloatBits((int)(192f * percent * lp), 0, (int)(192f * invPercent * lp), 255);
		verticies[15] = Color.toFloatBits((int)(255f * percent * lp), 0, (int)(255f * invPercent * lp), 255);
		squareMesh.setVertices(verticies);
	}
	
	public void updateColor() {
		//calculate color
		float percent = health / c.ZOMBIE_HEALTH;
		if (percent < 0f) {
			percent = 0f;
		}
		float invPercent = 1.0f - percent;
		verticies[3] = Color.toFloatBits((int)(128f * percent), 0, (int)(128f * invPercent), 255);
		verticies[7] = Color.toFloatBits((int)(192f * percent), 0, (int)(192f * invPercent), 255);
		verticies[11] = Color.toFloatBits((int)(192f * percent), 0, (int)(192f * invPercent), 255);
		verticies[15] = Color.toFloatBits((int)(255f * percent), 0, (int)(255f * invPercent), 255);
		squareMesh.setVertices(verticies);
	}
	
}