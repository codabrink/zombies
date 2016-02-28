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
import com.zombies.zombie.Zone;

public class Zombie extends Unit implements Collideable{
	private long lastAttack = System.currentTimeMillis();
	private Player player;
	private Random random = new Random();
	public Zone zone;
    private String state = "dormant"; // dormant -> loaded -> active

	public Zombie(GameView view, Box box, Vector2 position) {
		super();
		this.box = box;
		player = view.getPlayer();

        // I kinda hate you, Java...
        storedPosition = position;
        storedBodData = new BodData("zombie", this);

		speed = C.ZOMBIE_SPEED;
        color = new Color(1, 0, 0, 1);
		health = C.ZOMBIE_HEALTH;

        this.updateZone();
	}

    public void setState(String state) {
        if (state == "dead") {
            unload();
            view.removeActiveZombie(this);
            zone.removeZombie(this);
            view.s.zombieKills ++;
            view.s.score += C.SCORE_ZOMBIE_KILL;
        } else if (state == "dormant") {
            unload();
        } else if (state == "loaded") {
            load();
        } else if (state == "active") {
            load();
        }
    }

    public void die(Unit u) {
        setState("dead");
    }

    @Override
	public void load() {
        if (body != null)
            return;

        shape = new CircleShape();
		bDef.allowSleep = true;
		bDef.fixedRotation = true;
		bDef.linearDamping = C.LINEAR_DAMPING;
		bDef.position.set(storedPosition);
		bDef.type = BodyType.DynamicBody;

		body = view.getWorld().createBody(bDef);
		shape.setRadius(C.ZOMBIE_SIZE * 0.75f);
		MassData mass = new MassData();
		mass.mass = .1f;
        body.setMassData(mass);
		body.setUserData(storedBodData);

		fDef.shape = shape;
		fDef.density = 0.1f;

		body.createFixture(fDef);

        view.addActiveZombie(this);
	}

	private void attack() {
		mPos = attack.getBody().getPosition();
		if (System.currentTimeMillis() < lastAttack + C.ZOMBIE_ATTACK_RATE) { return; }
		if (body.getPosition().dst(attack.getBody().getPosition()) < C.ZOMBIE_SIZE * 2) {
			attack.hurt(C.ZOMBIE_STRENGTH, this);
			lastAttack = System.currentTimeMillis();
		}
	}
	
	public void attack(Unit u) {
		attack = u;
	}

    @Override
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        if (state == "dead" ||	box.getRoom() != view.player.getRoom() ||	body == null) return;

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
		this.hurt(C.BULLET_DAMAGE_FACTOR, u);
	}
	
	private Vector2 randomClosePoint() {
		float nx = body.getPosition().x + random.nextFloat() * 20 - 10;
		float ny = body.getPosition().y + random.nextFloat() * 20 - 10;
		return new Vector2(nx, ny);
	}

	@Override
	public void update(int frame) {
		super.update(frame);
        if (state != "dormant") {
            if (storedPosition.dst(GameView.m.getPlayer().getBody().getPosition()) < 50)
                setState("active");
            else
                return;
        }

        if (body.getPosition().dst(view.getPlayer().getBody().getPosition()) > C.ZONE_SIZE * 2)
            setState("dormant");

		//handle sleeping
		if (attack == null) {
			if (random.nextFloat() <= C.ZOMBIE_AWARENESS && this.isVisionClear()) {
				if (random.nextBoolean() == true) {
					attack = view.getPlayer();
				} else {
					attack = view.getPlayer().randomSurvivor();
				}
			}
		}

		//update box
		updateZone();
		box = zone.getBox(body.getPosition().x, body.getPosition().y);

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

    public void updateZone() {
        Zone z;
        if (body != null){
            z = Zone.getZone(body.getPosition().x, body.getPosition().y);
        } else {
            z = Zone.getZone(storedPosition.x, storedPosition.y);
        }
		z.addZombie(this);
    }

	@Override
	public void move() {
        body.applyForce(mPos.sub(body.getPosition()).setLength(C.ZOMBIE_AGILITY), new Vector2(), true);
		if (body.getLinearVelocity().len() > C.ZOMBIE_SPEED) { //Zombie is going too fast
            body.setLinearVelocity(body.getLinearVelocity().setLength(C.ZOMBIE_SPEED));
		}
	}

	public void setZone(Zone z) {
		zone.removeZombie(this);
		z.addZombie(this);
	}
}