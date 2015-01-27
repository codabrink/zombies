package com.zombies;

import java.util.LinkedList;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;

public class Survivor extends Unit implements Collideable {

	private long beginAttacks = System.currentTimeMillis();
	private GameView view;
	private BodyDef bDef = new BodyDef();
	private FixtureDef fDef = new FixtureDef();
	private boolean found = false;
	private long lastShot;
	protected LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	private Unit target = null;
	private Random random = new Random();
	private short GROUP = -4;
	private long lastAttack = System.currentTimeMillis();
	private int updateInt;
	private long fireRate;
    private ShapeRenderer shapeRenderer;
	
	public Survivor(GameView view, Box box, Vector2 position) {
		super(view);
		shapeRenderer = view.getShapeRenderer();
		updateInt = random.nextInt(c.UPDATE_LIGHTING_INTERVAL);
		
		lastShot = System.currentTimeMillis();
		speed = c.PLAYER_SPEED;
		
		fireRate = c.SURVIVOR_FIRE_RATE + ((long)random.nextFloat() * 1000l);
		
		bDef.allowSleep = true;
		bDef.fixedRotation = true;
		bDef.linearDamping = c.LINEAR_DAMPING;
		bDef.position.set(position);
		bDef.type = BodyType.DynamicBody;
		
		body = view.getWorld().createBody(bDef);
		shape.setRadius(c.PLAYER_SIZE * 0.75f);
		MassData mass = new MassData();
		mass.mass = .1f;
		body.setMassData(mass);
		body.setUserData(new BodData("survivor", this));
		
		fDef.shape = shape;
		fDef.density = 0.1f;
		fDef.filter.groupIndex = GROUP;
		
		body.createFixture(fDef);
		this.box = box;
		
		health = c.SURVIVOR_HEALTH;
		
		this.view = view;
	}
	
	private void AI() {
		handleShots();
		if (mPos == null) {
			mPos = randomDirection();
		}
		this.move();
		if (!this.canChangeMPos())
			return;
		if (box != view.getPlayer().getBox()) {
			//move left
			float dx = view.getPlayer().getBody().getPosition().x - body.getPosition().x;
			float dy = view.getPlayer().getBody().getPosition().y - body.getPosition().y;
			if (Math.abs(dx) > Math.abs(dy)) {
				if (dx < 0) {
					if (box.getWall(3) == null) {
						mPos = box.getBox(3).randomPoint();
					}
					else {
						mPos = new Vector2(box.getX(), box.getY() + c.BOX_HEIGHT / 2f);
					}
				}
				//move right
				else {
					if (box.getWall(1) == null) {
						mPos = box.getBox(1).randomPoint();
					}
					else {
						mPos = new Vector2(box.getX() + c.BOX_WIDTH, box.getY() + c.BOX_HEIGHT / 2f);
					}
				}
			}
			else {
				//move up
				if (dy < 0) {
					if (box.getWall(0) == null) {
						mPos = box.getBox(0).randomPoint();
					}
					else {
						mPos = new Vector2(box.getX() + c.BOX_WIDTH / 2f, box.getY());
					}
				}
				//move down
				else {
					if (box.getWall(2) == null) {
						mPos = box.getBox(2).randomPoint();
					}
					else {
						mPos = new Vector2(box.getX() + c.BOX_WIDTH / 2f, box.getY() + c.BOX_HEIGHT);
					}
				}
			}
		}
		else {
			mPos = randomDirection();
		}
		
	}
	
	@Override
	public void kill(Unit u) {
		if (dead) return;
		view.addDyingZombie(new DyingZombie(view, body.getPosition()));
		view.getPlayer().killSurvivor(this);
		dead = true;
		view.s.survivorsLost ++;
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
			kill(u);
		}
		lastAttack = System.currentTimeMillis();
	}
	
	public void draw() {
		if (dead) {
			return;
		}
		for (Bullet b: bullets) {
			b.draw();
		}

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(body.getPosition().x - 0.5f, body.getPosition().y - 0.5f, 1, 1);
        shapeRenderer.end();
	}
	
	public Box getBox() {
		return box;
	}
	
	public Room getRoom() {
		return box.getRoom();
	}
	
	public void handleCollision(Fixture f) {
		String type = ((BodData)f.getBody().getUserData()).getType();
		Object o = ((BodData)f.getBody().getUserData()).getObject();
	}
	
	public void handleShots() {
		if (box.getRoom().isAlarmed()) {
			if (target == null) {
				if (playerInRoom()) {
					if (box.getUnits().size() > 0) {
						Unit u = box.randomZombie();
						if (u != view.getPlayer())
							target = u;
					}
					else {
						return;
					}
				}
				else {
					return;
				}
			}
			else {
				if (!playerInRoom() || target.isDead()) {
					target = null;
					return;
				}
				if (System.currentTimeMillis() > lastShot + fireRate) {
                    Vector2 shot = target.getBody().getPosition().sub(body.getPosition()).scl(100);
					if (bullets.size() > c.MAX_BULLETS) {
						bullets.getFirst().setPosition(new Vector2(body.getPosition().x, body.getPosition().y));
						bullets.getFirst().setVelocity(shot);
						// throw bullet to back of list
						bullets.add(bullets.removeFirst().refresh());
					} else {
						bullets.add(new Bullet(view, this, GROUP, new Vector2(body.getPosition().x, body.getPosition().y), shot));
					}
					
					lastShot = System.currentTimeMillis();
					view.s.survivorShots ++;
				}
			}
		}
	}
	
	public boolean isFound() {
		return found;
	}

	public boolean playerInRoom() {
		if (box.getRoom() == view.getPlayer().getBox().getRoom()) {
			return true;
		}
		return false;
	}
	
	public void wake() {
		found = true;
	}
	
	@Override
	public void update() {
		if (dead) {
			return;
		}
		if (found) {
			AI();
		}
		else if (body.getPosition().dst(view.getPlayer().getBody().getPosition()) < c.SURVIVOR_WAKE_DIST) {
			found = true;
			box.addDumpList(this);
			view.getPlayer().addSurvivor(this);
			view.s.survivorsFound ++;
			view.s.score += c.SCORE_FIND_SURVIVOR;
		}
		
		for (Bullet b: bullets) {
			b.update();
		}
		
		capSpeed();
		this.updateVerticies();
	}
	
	@Override
	public void victory() {
		target = null;
	}
	
	private void updateVerticies() {
		float percent = health / c.SURVIVOR_HEALTH;
		if (percent < 0f) {
			percent = 0f;
		}
		float invPercent = 1.0f - percent;
		verticies[3] = Color.toFloatBits((int)(128f * invPercent), 0, (int)(128f * percent), 255);
		verticies[7] = Color.toFloatBits((int)(192f * invPercent), 0, (int)(192f * percent), 255);
		verticies[11] = Color.toFloatBits((int)(192f * invPercent), 0, (int)(192f * percent), 255);
		verticies[15] = Color.toFloatBits((int)(255f * invPercent), 0, (int)(255f * percent), 255);
		squareMesh.setVertices(verticies);
	}
	
}