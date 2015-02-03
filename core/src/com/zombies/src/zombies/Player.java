package com.zombies.src.zombies;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.zombies.src.guns.Pistol;

import java.util.ArrayList;
import java.util.Random;

public class Player extends Unit implements Collideable {

	private long beginAttacks = System.currentTimeMillis();
	private C c;
	private long lastAttack = System.currentTimeMillis();
	private long lastShot = System.currentTimeMillis();
	private float mX=0, mY=0;
	private Room oldRoom = null;
	private Random random = new Random();
	private ArrayList<Survivor> survivors = new ArrayList<Survivor>();
	private ArrayList<Survivor> survivorKill = new ArrayList<Survivor>();
	private GameView view;
	private float angle = 0;
	private long lastAngleSet = System.currentTimeMillis();
	private long angleLast = 1000l;

    private float radius, diameter;

	private int zombieKillCount = 0;

	public Player(GameView view, Box box) {
		super(view);
		this.view = view;
		this.box = box;
		this.c = view.c;

        radius = c.PLAYER_SIZE * 0.5f;
        diameter = c.PLAYER_SIZE;

		GROUP = -1;
		
		speed = c.PLAYER_SPEED;
		
		health = c.PLAYER_HEALTH;
		
		guns.add(new Pistol(view, this, 50));

		bDef.allowSleep = false;
		bDef.fixedRotation = true;
		bDef.linearDamping = c.LINEAR_DAMPING;
		bDef.position.set(box.randomPoint());
		bDef.type = BodyType.DynamicBody;
		
		body = view.getWorld().createBody(bDef);
		shape.setRadius(c.PLAYER_SIZE * 0.75f);
		MassData mass = new MassData();
		mass.mass = .1f;
		body.setMassData(mass);
		body.setUserData(new BodData("player", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;
		fDef.filter.groupIndex = GROUP;
		
		body.createFixture(fDef);
	}
	
	public float getHealth() {
		return health;
	}
	
	@Override
	public void addGun(Gun g) {
		for (Gun gun: guns) {
			if (gun.isType(g.getType())) {
				gun.addAmmo(g.getAmmo());
				view.mh.addMessage(new Message(view, "Picked up " + String.valueOf(g.getAmmo()) + " shells for the " + g.getType() + "."));
				return;
			}
		}
		guns.add(g);
		gunIndex = guns.size() - 1;
		view.mh.addMessage(new Message(view, "You picked up a shotgun."));
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
        body.setTransform(body.getPosition().x, body.getPosition().y, (float)Math.toRadians(angle));
		lastAngleSet = System.currentTimeMillis();
	}
	
	public void suggestAngle(float angle) {
		if (System.currentTimeMillis() > lastAngleSet + angleLast) {
			this.angle = angle;
		}
	}
	
	public void addSurvivor(Survivor s) {
		survivors.add(s);
	}
	
	public void addZombieKill() {
		zombieKillCount++;
	}
	
	public void applyMove() {
//		body.applyForce(new Vector2(mX, mY), new Vector2());
		Vector2 v = new Vector2(mX, mY);
		if (v.len() > c.PLAYER_SPEED) {
			body.setLinearVelocity(v.scl(c.PLAYER_SPEED));
		} else {
			body.setLinearVelocity(new Vector2(mX, mY));
		}
	}
	
	public void clearOldRoom() {
		oldRoom = null;
	}
	
	public Body getBody() {return body;}
	
	public Box getBox() {
		return box;
	}
	
	public Gun getGun() {
		return guns.get(gunIndex);
	}
	
	public float getHealthPercent() {
		return health / c.PLAYER_HEALTH;
	}
	
	public Room getRoom() {
		return box.getRoom();
	}
	
	public ArrayList<Survivor> getSurvivors() {
		return survivors;
	}
	
	public Vector2 getVector() {
		return new Vector2(body.getPosition().x, body.getPosition().y);
	}
	
	public float getX() {return body.getPosition().x;}
	
	public float getY() {return body.getPosition().y;}
	
	public int getZombieCount() {
		return zombieKillCount;
	}
	@Override
	public void handleCollision(Fixture f) {
//		String type = ((BodData)f.getBody().getUserData()).getType();
//		Object o = ((BodData)f.getBody().getUserData()).getObject();
		
	}
	private void handleHealth() {
//		restore health
//		if (health < c.PLAYER_HEALTH && System.currentTimeMillis() - lastAttack > c.RESTORE_HEALTH_TIME) {
//			health += c.HEALTH_RESTORE_RATE;
//		}
//		cap health
		if (health > c.PLAYER_HEALTH) {
			health = c.PLAYER_HEALTH;
		} else if (health < 0) {
			health = 0;
		}
	}
	
	@Override
	public void hurt(float zombieStrength, Unit u) {
		if (health >= c.PLAYER_HEALTH) {
			beginAttacks = System.currentTimeMillis();
		}
		if (view.getPlayer().getRoom() != box.getRoom()) {
			body.setAwake(false);
			return;
		}
		health -= zombieStrength;
		view.s.damageTaken += zombieStrength;
		if (health < 0) {
			die(u);
    }
		lastAttack = System.currentTimeMillis();
	}
	
	public boolean isBody(Body b) {
		if (b == body)
			return true;
		return false;
	}
	
	//@Override
	//public void die(Unit u) {view.main.endGame();}
	
	public void setMove(float x, float y) {
		if (Math.abs(x) < c.TILT_IGNORE) {
			mX = 0;
		} else {
			mX = (x - c.TILT_IGNORE) * c.TILT_SENSITIVITY;
		}
		if (Math.abs(y) < c.TILT_IGNORE) {
			mY = 0;
		} else {
			mY = (y - c.TILT_IGNORE) * c.TILT_SENSITIVITY;
		}
		this.suggestAngle((float)Math.toDegrees(Math.atan2(y, x)));
	}
	
	public Vector2 getDirection() {
		float ax = (float)Math.cos(Math.toRadians(this.angle));
		float ay = (float)Math.sin(Math.toRadians(this.angle));
		return new Vector2(ax, ay);
	}
	
	public Survivor randomSurvivor() {
		if (!survivors.isEmpty())
			return survivors.get(random.nextInt(survivors.size()));
		return null;
	}
	
	public Unit randomUnit() {
		int r = random.nextInt(survivors.size() + 1);
		if (r == survivors.size()) {
			return this;
		} else {
			return survivors.get(r);
		}
	}
	
	public void killSurvivor(Survivor s) {
		survivorKill.add(s);
	}
	
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
		box.getRoom().drawFloors(spriteBatch, shapeRenderer);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(body.getPosition().x - radius, body.getPosition().y - radius, radius, radius, diameter, diameter, 1, 1, (float)Math.toDegrees(body.getAngle()));

        float gunOffsetX = radius * 0.5f;
        float gunOffsetY = 0;
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
        shapeRenderer.rect(body.getPosition().x + gunOffsetX, body.getPosition().y + gunOffsetY, -gunOffsetX, -gunOffsetY, radius,   diameter, 1, 1, (float)Math.toDegrees(body.getAngle()) - 90);
        shapeRenderer.end();

        for (Gun g: guns) {
            g.draw(spriteBatch, shapeRenderer);
        }
        for (Survivor s: survivors) {
            s.draw(spriteBatch, shapeRenderer);
        }

        box.getRoom().drawRoom(spriteBatch, shapeRenderer);
		
		if (oldRoom != null) {
			oldRoom.drawWalls();
		}
		
		//update things
		box.getRoom().primaryUpdate();
		update();
	}
	
	public void renderGunInfo(SpriteBatch spriteBatch) {
		int count = 0;
		for (Gun g: guns) {
			count++;
			g.renderGunBox(count, spriteBatch);
		}
	}
	
	public void handleTouch(float x, float y) {
		for (Gun g: guns) {
			g.handleTouch(x, y);
		}
	}
	
	@Override
	public void setBox(Box b) {
		if (b.getRoom() != box.getRoom()) {
			oldRoom = box.getRoom();
		}
		box = b;
	}
	
	public void setGun(Gun g) {
		gunIndex = guns.indexOf(g);
	}
	
	public void shoot(Vector2 direction) {
		if (!guns.isEmpty()) {
			guns.get(gunIndex).shoot(direction);
			if (guns.get(gunIndex).isEmpty()) {
				for (int i=0;i<guns.size();i++) {
					if (!guns.get(i).isEmpty()) {
						view.mh.addMessage(new Message(view, "Out of bullets for the " + guns.get(gunIndex).getType() + "."));
						gunIndex = i;
						view.mh.addMessage(new Message(view, "Switched to " + guns.get(gunIndex).getType() + "."));
						return;
					}
				}
			}
		}
	}
	
	@Override
	public void update() {
		box.updateZombieRecords(this);

        //TODO this is temporary
        health = c.PLAYER_HEALTH;

//		applyMove();
		box.updatePlayerRecords();
		
		if (oldRoom != null) {
			oldRoom.updateAlpha();
		}
		
		this.handleHealth();

        for (Gun g: guns) {
            g.update();
        }

		for (Survivor s: survivors) {
			s.update();
			s.getBox().updateSurvivorRecords(s);
		}
		
		for(Survivor s: survivorKill) {
			
			survivors.remove(s);
		}
		survivorKill.clear();
		
		for (Bullet b: bullets) {
			b.update();
		}

		capSpeed();
		
		if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
			this.applyMove();
		}
	}
}