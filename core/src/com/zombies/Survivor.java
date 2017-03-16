package com.zombies;

import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.zombies.data.D;
import com.zombies.guns.Pistol;
import com.zombies.interfaces.Collideable;

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

        body = D.world.createBody(bDef);
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
        updateZone();
    }
    @Override
    protected void updateZone() {
        zone = Zone.getZone(body.getPosition());
        zone.addObject(this);
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

    @Override
    public void die(Unit u) {
        if (state == State.DEAD) return;
        view.getPlayer().removeSurvivor(this);
        view.stats.survivorsLost ++;
    }

    @Override
    public void hurt(float zombieStrength, Unit u) {
        if (health >= C.PLAYER_HEALTH) {
            beginAttacks = System.currentTimeMillis();
        }
        health -= zombieStrength;
        if (health < 0) {
            //TODO let survivors die
            //die(u);
    }
        lastAttack = System.currentTimeMillis();
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        if (state == State.DEAD) {
            return;
        }
        gun.draw(spriteBatch, shapeRenderer, modelBatch);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 1, 1);
        shapeRenderer.rect(body.getPosition().x - 0.5f, body.getPosition().y - 0.5f, 1, 1);
        shapeRenderer.end();
    }

    public void handleCollision(Fixture f) {
        String type = ((BodData)f.getBody().getUserData()).getType();
        Object o = ((BodData)f.getBody().getUserData()).getObject();
    }

    public void handleShots() {

    }

    public boolean isFound() {
        return found;
    }

    public boolean playerInRoom() {
        return false;
    }

    public void wake() {
        found = true;
    }

    @Override
    public void update() {
        super.update();
        if (state == State.DEAD)
            return;
        if (state == State.FOUND)
            AI();
        else if (body.getPosition().dst(view.getPlayer().getBody().getPosition()) < C.SURVIVOR_WAKE_DIST) {
            state = State.FOUND;
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
