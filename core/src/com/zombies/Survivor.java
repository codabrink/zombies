package com.zombies;

import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.guns.Pistol;

public class Survivor extends Unit implements Collideable {

    private long beginAttacks = System.currentTimeMillis();
    private BodyDef bDef = new BodyDef();
    private FixtureDef fDef = new FixtureDef();
    private boolean found = true;
    private long lastShot;
    protected LinkedList<Bullet> bullets = new LinkedList<Bullet>();
    protected LinkedList<Vector2> pointsOfInterest = new LinkedList<Vector2>(); // Points of interest
    private Unit target = null;
    private Random random = new Random();
    private short GROUP = -4;
    private long lastAttack = System.currentTimeMillis();
    private int updateInt;
    private long fireRate;
    private Pistol gun;

    public Survivor(Vector2 position) {
        super();
        updateInt = random.nextInt(C.UPDATE_LIGHTING_INTERVAL);

        gun = new Pistol(this, -1);
        lastShot = System.currentTimeMillis();
        speed = C.PLAYER_SPEED;

        fireRate = C.SURVIVOR_FIRE_RATE + ((long)random.nextFloat() * 1000l);

        bDef.allowSleep = true;
        bDef.fixedRotation = true;
        bDef.linearDamping = C.LINEAR_DAMPING;
        bDef.position.set(position);
        bDef.type = BodyType.DynamicBody;

        body = view.getWorld().createBody(bDef);
        shape.setRadius(C.PLAYER_SIZE * 0.75f);
        MassData mass = new MassData();
        mass.mass = .1f;
        body.setMassData(mass);
        body.setUserData(new BodData("survivor", this));

        fDef.shape = shape;
        fDef.density = 0.1f;
        fDef.filter.groupIndex = GROUP;

        body.createFixture(fDef);

        health = C.SURVIVOR_HEALTH;
        updateBox(); // updateBox calls updateZone
    }

    @Override
    protected void updateBox() {
        Vector2 position = (body != null ? body.getPosition() : storedPosition);
        updateZone();
        box = zone.getBox(position.x, position.y);
        box.addUnit(this);
    }
    @Override
    protected void updateZone() {
        Vector2 position = (body != null ? body.getPosition() : storedPosition);
        zone = Zone.getZone(position.x, position.y);
        zone.addUnit(this);
    }


    private void AI() {
        handleShots();
        if (!this.canChangeMPos())
            return;

        if (isFurtherThanFromPlayer(40f)) {
            body.setTransform(pointNearPlayer(), 0);
            pointsOfInterest.clear();
        } else if (isFurtherThanFromPlayer(10f) || isCloserThanFromPlayer(6f)) {
            mPos = pointNearPlayer();
        }
        if (pointsOfInterest.size() > 0 || mPos != null) {
            this.move();
            mPos = null;
        }
    }

    private Vector2 pointNearPlayer() {
        //move closer to player
        Vector2 dP = new Vector2(
                body.getPosition().x - view.getPlayer().getX() + (random.nextFloat() * 2 - 1f),
                body.getPosition().y - view.getPlayer().getY() + (random.nextFloat() * 2 - 1f)
        );
        dP.setLength(8 + (random.nextFloat() * 4 - 2f));
        return view.getPlayer().getBody().getPosition().cpy().add(dP);
    }

    @Override
    public void move() {
        if (pointsOfInterest.size() > 0) {
            // NOTE: With this if here, there is one frame where the survivor doesn't move.
            body.applyForce(pointsOfInterest.peek().cpy().sub(body.getPosition()).setLength(8f), new Vector2(), true);
            if (pointsOfInterest.peek().dst(body.getPosition()) < 1) {
                pointsOfInterest.remove();
            }
        } else {
            body.applyForce(mPos.sub(body.getPosition()).setLength(8f), new Vector2(), true);
        }
    }

    public boolean isFurtherThanFromPlayer(float distance) {
        return view.getPlayer().getBody().getPosition().dst(body.getPosition()) >= distance;
    }

    public boolean isCloserThanFromPlayer(float distance) {
        return view.getPlayer().getBody().getPosition().dst(body.getPosition()) < distance;
    }

    public boolean zombiesBetweenSelfAndPlayer() {
        return false;
    }

    public void pushPointOfInterest(Vector2 point) {
        pointsOfInterest.push(point);
    }

    @Override
    public void die(Unit u) {
        if (state == "dead") return;
        view.getPlayer().removeSurvivor(this);
        view.stats.survivorsLost ++;
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
        if (health < 0) {
            //TODO let survivors die
            //die(u);
    }
        lastAttack = System.currentTimeMillis();
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        if (state == "dead") {
            return;
        }
        gun.draw(spriteBatch, shapeRenderer);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 1, 1);
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
                    if (true)
                        return; //TODO fix fighting
                    Vector2 shot = target.getBody().getPosition().sub(body.getPosition()).setLength(100);
                    gun.shoot(shot);

                    //Switched over to a gun system
                    //bullets.add(new Bullet(view, this, GROUP, new Vector2(body.getPosition().x, body.getPosition().y), shot));

                    lastShot = System.currentTimeMillis();
                    view.stats.survivorShots ++;
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
    public void update(int frame) {
        super.update(frame);
        if (state == "dead")
            return;
        if (state == "found")
            AI();
        else if (body.getPosition().dst(view.getPlayer().getBody().getPosition()) < C.SURVIVOR_WAKE_DIST) {
            setState("found");
            box.removeUnit(this);
            view.getPlayer().addSurvivor(this);
            view.stats.survivorsFound ++;
            view.stats.score += C.SCORE_FIND_SURVIVOR;
        }

        for (Bullet b: bullets) {
            b.update();
        }

        capSpeed();
        this.updateVerticies();

        updateZone();
        updateBox();
    }

    @Override
    public void victory() {
        target = null;
    }

    private void updateVerticies() {
    }

}
