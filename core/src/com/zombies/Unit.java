package com.zombies;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Unit {
	protected Unit attack = null;
	protected BodyDef bDef = new BodyDef();
	protected Body body;
	protected Box box;
	protected LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	protected C c;
	protected boolean dead = false;
	protected float diffX, diffY;
	protected FixtureDef fDef = new FixtureDef();
	protected short GROUP;
	protected int gunIndex = 0;
	protected ArrayList<Gun> guns = new ArrayList<Gun>();
	protected float health;
	protected long holdMPos = System.currentTimeMillis();
	protected Vector2 mPos;
	private LinkedList<Fixture> obstacles = new LinkedList<Fixture>();
	protected CircleShape shape;
	protected float speed;
	protected GameView view;
    protected Color color;

	private RayCastCallback vision = new RayCastCallback() {
		@Override
		public float reportRayFixture(Fixture f, Vector2 point, Vector2 normal, float fraction) {
			obstacles.add(f);
			return 0;
		}
	};

	public Unit(GameView view) {
		this.c = view.c;
		shape = new CircleShape();
		this.view = view;
	}
	
	public void addGun(Gun g) {
		for (Gun gun: guns) {
			if (gun.isType(g.getType())) {
				gun.addAmmo(g.getAmmo());
				return;
			}
		}
		guns.add(g);
		gunIndex = guns.size() - 1;
	}
	
	protected boolean canChangeMPos() {
		return (System.currentTimeMillis() > holdMPos);
	}
	
	protected void capSpeed() {
		if (body.getLinearVelocity().len() > speed) {
            body.setLinearVelocity(body.getLinearVelocity().setLength(c.PLAYER_SPEED));
		}
	}
	
	public void destroy() {
        dead = true;
		shape.dispose();
        body.setUserData(null);
		view.getWorld().destroyBody(body);
        body = null;
	}
	
	public float distance(Unit z) {
		diffX = body.getPosition().x - z.getX();
		diffY = body.getPosition().y - z.getY();
		return (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
	}

    public Vector2 rotatePoint(float x, float y, float r) {
        float nx = (float)(x * Math.cos(r) - y * Math.sin(r));
        float ny = (float)(x * Math.sin(r) - y * Math.cos(r));
        return new Vector2(nx, ny);
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {}

	public Body getBody() {return body;}
	public Box getBox() {
		return box;
	}
	
	public LinkedList<Bullet> getBullets() {
		return bullets;
	}
	
	public Fixture getFixture() {
		return body.getFixtureList().get(0);
	}
	
	public short getGroup() {
		return GROUP;
	}
	
	public float getX() {
		return body.getPosition().x;
	}
	
	public float getY() {
        return body.getPosition().y;
	}
	
	public void hurt(float zombieStrength, Unit u) {
		if (view.getPlayer().getRoom() != box.getRoom()) {
			body.setAwake(false);
			return;
		}
		health -= zombieStrength;
		if (health < 0 && !dead) {
			u.victory();
			die(u);
		}
		sick(u);
	}

	public float getHealth(){
		return health;
	}
	
	public void heal(float h) {
		health += h;
		if (health > c.PLAYER_HEALTH) {
			health = c.PLAYER_HEALTH;
		}
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public boolean isVisionClear() {
		obstacles.clear();
		view.getWorld().rayCast(vision, body.getPosition(), view.getPlayer().getBody().getPosition());
		for (Fixture f: obstacles) {
			if (f == null || f.getBody() == null || f.getBody().getUserData() == null)
				return false;
			String type = ((BodData)f.getBody().getUserData()).getType();
			if (type != "zombie" && type != "player") {
				return false;
			}
		}
		return true;
	}
	
	public void die(Unit u) {}
	
	protected void move() {
		body.applyForce(mPos.sub(body.getPosition().setLength(c.ZOMBIE_AGILITY)), new Vector2(), true);
	}
	
	public Vector2 randomDirection() {
		return box.randomPoint();
	}
	
	public void setBox(Box b) {
		box = b;
	}
	
	protected void shove(float x, float y, long duration) {
		float nx = body.getPosition().x + x;
		float ny = body.getPosition().y + y;
		mPos = new Vector2(nx, ny);
	}

    public ArrayList<Vector2> pathFind(Vector2 point) {

        return new ArrayList<Vector2>();
    }

	public void sick(Unit a) {
		if (attack == null)
			attack = a;
	}
	
	public void update() {}
	
	public void victory() {}
}
