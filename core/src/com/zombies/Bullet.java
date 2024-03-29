package com.zombies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.zombies.data.D;

import java.util.ArrayList;
import java.util.Arrays;

public class Bullet {
	private Unit unit;
	private long createTime = System.currentTimeMillis();
    private long lifeTime = 1000l;
    private Vector2 position;
    private Vector2 originalPosition;
    private Vector2 direction;
    private String[] stoppingObjects = {"zombie", "crate", "wall"};
	private GameView view;
	private float speed = 15f;
    private RayCastCallback callback;
    private Gun gun;
    private float shotSpread = 10f;

    private float destinedTrajectoryLength;

    private Fixture stopFixture;

	public Bullet(final GameView view, Unit unit, short group, Gun gun, final Vector2 position, Vector2 direction) {
        this.view = view;
        this.originalPosition = position.cpy();
        this.position = position.cpy();
        this.direction = direction.cpy().setLength(speed);
        this.unit = unit;
        this.gun = gun;

        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                // If the fixture is in the stoppingObjects list
                BodData bodData = ((BodData) fixture.getBody().getUserData());
                if (bodData.getType().equals("wall")) {
                    //((com.zombies.map.room.Wall)bodData.getObject()).createHole(point, 2 * C.SCALE);
                }

                if (bodData != null && Arrays.asList(stoppingObjects).contains(bodData.getType())) {
                    destinedTrajectoryLength = position.dst(point);
                    stopFixture = fixture;
                    return 0;
                }
                return 0;
            }
        };
        view.clearDebugDots();
    }

    public ArrayList<Unit> unitsInBulletRange() {
        ArrayList<Unit> units = new ArrayList<Unit>();
        Vector2 p1 = position.cpy().add(direction.cpy());
        Vector2 p2 = position.cpy().add(direction.cpy().setLength(30 * C.SCALE));
        D.world.rayCast(callback, p2, p1);

        ArrayList<Vector2> hitBoxCorners = new ArrayList<Vector2>();
        Vector2 behindPlayer = position.cpy().sub(direction.cpy().setLength(-C.PLAYER_SIZE * 6 * C.SCALE));
        hitBoxCorners.add(behindPlayer.cpy().add(direction.cpy().rotate(-shotSpread).setLength(C.PLAYER_SIZE * 6 * C.SCALE)));
        hitBoxCorners.add(behindPlayer.cpy().add(direction.cpy().rotate(-shotSpread).setLength(30 * C.SCALE + C.PLAYER_SIZE * 2)));
        hitBoxCorners.add(behindPlayer.cpy().add(direction.cpy().rotate(shotSpread).setLength(30 * C.SCALE + C.PLAYER_SIZE * 2)));
        hitBoxCorners.add(behindPlayer.cpy().add(direction.cpy().rotate(shotSpread).setLength(C.PLAYER_SIZE * 6 * C.SCALE)));

        if (C.DEBUG) view.addDebugDots(hitBoxCorners.get(0), hitBoxCorners.get(1));
        if (C.DEBUG) view.addDebugDots(hitBoxCorners.get(2), hitBoxCorners.get(3));

        return units;
    }

    public void update() {
        //move the bullet
        position.add(direction);

        if (stopFixture == null) return;

        if (originalPosition.dst(position) > destinedTrajectoryLength || System.currentTimeMillis() > createTime + lifeTime) {
            gun.appendKillBullets(this);
        }
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rect(position.x - C.BULLET_RADIUS, position.y - C.BULLET_RADIUS, C.BULLET_RADIUS * 2, C.BULLET_RADIUS * 2);
        shapeRenderer.end();
        update();
	}

    private boolean pointInTrapezoid (Vector2 pt, ArrayList<Vector2> list) {
        return pointInTriangle(pt, list.get(0), list.get(1), list.get(3)) || pointInTriangle(pt, list.get(1), list.get(2), list.get(3));
    }

    private boolean pointInTriangle (Vector2 pt, Vector2 v1, Vector2 v2, Vector2 v3) {
        boolean b1, b2, b3;

        b1 = sign(pt, v1, v2) < 0.0f;
        b2 = sign(pt, v2, v3) < 0.0f;
        b3 = sign(pt, v3, v1) < 0.0f;

        return ((b1 == b2) && (b2 == b3));
    }

    private float sign (Vector2 p1, Vector2 p2, Vector2 p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

	public Unit getUnit() {
		return unit;
	}
}
