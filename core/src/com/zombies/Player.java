package com.zombies;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.guns.Pistol;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;

public class Player extends Unit implements Collideable {

	private long beginAttacks = System.currentTimeMillis();
	private long lastAttack = System.currentTimeMillis();
	private long lastShot = System.currentTimeMillis();
	private float mX=0, mY=0;
	private Random random = new Random();
	private ArrayList<Survivor> survivors = new ArrayList<Survivor>();
	private ArrayList<Survivor> survivorKill = new ArrayList<Survivor>();
	private float angle = 0;
	private long lastAngleSet = System.currentTimeMillis();
	private long angleLast = 1000l;
	private HealthBar healthBar;

    private float radius, diameter;

	private int zombieKillCount = 0;

	public Player(Box box) {
		super();
		this.box = box;

		healthBar = new HealthBar();
		box.getRoom().currentRoom();
        radius = C.PLAYER_SIZE * 0.5f;
        diameter = C.PLAYER_SIZE;

		GROUP = -1;
		
		speed = C.PLAYER_SPEED;
		
		health = C.PLAYER_HEALTH;
		
		guns.add(new Pistol(this, 50));

		bDef.allowSleep = false;
		bDef.fixedRotation = true;
		bDef.linearDamping = C.LINEAR_DAMPING;
		bDef.position.set(box.randomPoint());
		bDef.type = BodyType.DynamicBody;
		
		body = view.getWorld().createBody(bDef);
		shape.setRadius(C.PLAYER_SIZE * 0.75f);
		MassData mass = new MassData();
		mass.mass = .1f;
		body.setMassData(mass);
		body.setUserData(new BodData("player", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;
		fDef.filter.groupIndex = GROUP;
		
		body.createFixture(fDef);
        updateZone();
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


	public void addZombieKill() {
		zombieKillCount++;
	}
	
	public void applyMove() {
//		body.applyForce(new Vector2(mX, mY), new Vector2());
		Vector2 v = new Vector2(mX, mY);
		if (v.len() > C.PLAYER_SPEED) {
			body.setLinearVelocity(v.scl(C.PLAYER_SPEED));
		} else {
			body.setLinearVelocity(new Vector2(mX, mY));
		}
	}

	public Body getBody() {return body;}
	
	public Box getBox() {
		return box;
	}
	
	public Gun getGun() {
		return guns.get(gunIndex);
	}
	
	public float getHealthPercent() {
		return health / C.PLAYER_HEALTH;
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
//		if (health < C.PLAYER_HEALTH && System.currentTimeMillis() - lastAttack > C.RESTORE_HEALTH_TIME) {
//			health += C.HEALTH_RESTORE_RATE;
//		}
//		cap health
		if (health > C.PLAYER_HEALTH) {
			health = C.PLAYER_HEALTH;
		} else if (health < 0) {
			health = 0;
		}
	}
	
	@Override
	public void hurt(float zombieStrength, Unit u) {
		if (health >= C.PLAYER_HEALTH) {
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
        return b == body;
    }
	
	@Override
	public void die(Unit u) {view.main.setScreen(new EndView());}
	
	public void setMove(float x, float y) {
		if (Math.abs(x) < C.TILT_IGNORE) {
			mX = 0;
		} else {
			mX = (x - C.TILT_IGNORE) * C.TILT_SENSITIVITY;
		}
		if (Math.abs(y) < C.TILT_IGNORE) {
			mY = 0;
		} else {
			mY = (y - C.TILT_IGNORE) * C.TILT_SENSITIVITY;
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
        box.getRoom().draw(spriteBatch, shapeRenderer, frame, 1);

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

        healthBar.draw();
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
			for(Survivor s : survivors) {
                // TODO: update to exact point
				s.pushPointOfInterest(new Vector2(body.getPosition()));
			}
			b.getRoom().currentRoom();
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
	public void update(int frame) {
		box.getRoom().update(frame, 0);

        updateZone();
		zone.update(frame, 1);

//		applyMove();
		box.updatePlayerRecords();
		
		this.handleHealth();

        for (Gun g: guns) {
            g.update();
        }

		for (Survivor s: (ArrayList<Survivor>)survivors.clone()) {
			s.update(frame);

            if (s.getState() == "dead")
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

    @Override
    public void updateZone() {
        Zone z;
        if (body != null)
            z = Zone.getZone(body.getPosition().x, body.getPosition().y);
        else
            z = Zone.getZone(storedPosition.x, storedPosition.y);
        if (zone != z) {
			System.out.println("Log: Zone changed.");
			z.load();
		}
        zone = z;
    }
    public void removeSurvivor(Survivor s) {survivors.remove(s);}
    public void addSurvivor(Survivor s) {
        if (survivors.indexOf(s) == -1)
            survivors.add(s);
    }

}