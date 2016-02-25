package com.zombies;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
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

        // I kinda hate you, Java...
        storedPosition = position;
        storedBodData = new BodData("zombie", this);

		updateInt = random.nextInt(c.UPDATE_LIGHTING_INTERVAL);
		speed = c.ZOMBIE_SPEED;
        color = new Color(1, 0, 0, 1);
		health = c.ZOMBIE_HEALTH;
	}

    @Override
	public void load() {
        if (body != null)
            return;

        shape = new CircleShape();
		bDef.allowSleep = true;
		bDef.fixedRotation = true;
		bDef.linearDamping = c.LINEAR_DAMPING;
		bDef.position.set(storedPosition);
		bDef.type = BodyType.DynamicBody;

		body = view.getWorld().createBody(bDef);
		shape.setRadius(c.ZOMBIE_SIZE * 0.75f);
		MassData mass = new MassData();
		mass.mass = .1f;
		body.setMassData(mass);
		body.setUserData(storedBodData);

		fDef.shape = shape;
		fDef.density = 0.1f;

		body.createFixture(fDef);
        loaded = true;
	}

	private void attack() {
		mPos = attack.getBody().getPosition();
		if (System.currentTimeMillis() < lastAttack + c.ZOMBIE_ATTACK_RATE) { return; }
		if (body.getPosition().dst(attack.getBody().getPosition()) < c.ZOMBIE_SIZE * 2) {
			attack.hurt(c.ZOMBIE_STRENGTH, this);
			lastAttack = System.currentTimeMillis();
		}
	}
	
	public void attack(Unit u) {
		attack = u;
	}

    @Override
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        if (dead || !loaded) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(body.getPosition().x - 0.5f, body.getPosition().y - 0.5f, 1, 1);
        shapeRenderer.end();
	}
	
	@Override
	public void handleCollision(Fixture f) {
		String type = ((BodData)f.getBody().getUserData()).getType();
		Object o = ((BodData)f.getBody().getUserData()).getObject();
	}
	
	public void hitByBullet(Unit u) {
		this.hurt(c.BULLET_DAMAGE_FACTOR, u);
	}
	
	@Override
	public void die(Unit u) {
        if (dead) return;

		if (u == view.getPlayer()) {
			view.getPlayer().addZombieKill();
		}
		view.s.zombieKills ++;
		view.s.score += c.SCORE_ZOMBIE_KILL;
        dead = true;
	}
	
	private Vector2 randomClosePoint() {
		float nx = body.getPosition().x + random.nextFloat() * 20 - 10;
		float ny = body.getPosition().y + random.nextFloat() * 20 - 10;
		return new Vector2(nx, ny);
	}

	@Override
	public void update(int frame) {
		super.update(frame);
		if (dead) return;

		//handle sleeping
		if (attack == null) {
			if (random.nextFloat() <= c.ZOMBIE_AWARENESS && this.isVisionClear()) {
				if (random.nextBoolean() == true) {
					attack = view.getPlayer();
				} else {
					attack = view.getPlayer().randomSurvivor();
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
	}
	
	@Override
	public void move() {
        body.applyForce(mPos.sub(body.getPosition()).setLength(c.ZOMBIE_AGILITY), new Vector2(), true);
		if (body.getLinearVelocity().len() > c.ZOMBIE_SPEED) { //Zombie is going too fast
            body.setLinearVelocity(body.getLinearVelocity().setLength(c.ZOMBIE_SPEED));
		}
	}
}