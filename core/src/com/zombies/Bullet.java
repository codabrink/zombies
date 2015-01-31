package com.zombies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import java.util.Arrays;
import java.util.Vector;

public class Bullet {
	private Unit unit;
	private long createTime = System.currentTimeMillis();
	private C c;
    private Vector2 position;
    private Vector2 direction;
    private String[] stoppingObjects = {"zombie", "crate", "wall"};
	private GameView view;
	private float speed = 3f;
    private RayCastCallback callback;
    private Gun gun;

    private Vector2 stopPoint;
    private Fixture stopFixture;

	public Bullet(final GameView view, Unit unit, short group, Gun gun, Vector2 position, Vector2 direction) {
		c = view.c;
		this.view = view;
        this.position = position.cpy();
        this.direction = direction.cpy().setLength(speed);
		this.unit = unit;
        this.gun = gun;

        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                // If the fixture is in the stoppingObjects list
                BodData bodData = ((BodData)fixture.getBody().getUserData());
                if (bodData != null && Arrays.asList(stoppingObjects).contains(bodData.getType())) {
                    stopPoint = point.cpy();
                    stopFixture = fixture;
                    return 0;
                }
                return 0;
            }
        };
        view.getWorld().rayCast(callback, position.cpy().add(direction.cpy().setLength(c.PLAYER_SIZE)), position.cpy().add(direction.cpy().setLength(100)));
	}

    public void update() {
        Vector2 oldPosition = position.cpy();
        position.add(direction);

        if (stopPoint == null) return;
        //Is stopPoint in the middle of our movement delta?
        if (oldPosition.x < stopPoint.x && position.x > stopPoint.x ||
                oldPosition.x > stopPoint.x && position.x < stopPoint.x) {
            if (oldPosition.y < stopPoint.y && position.y > stopPoint.y ||
                    oldPosition.y > stopPoint.y && position.y < stopPoint.y) {
                gun.appendKillBullets(this);
            }
        }
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rect(position.x - c.BULLET_RADIUS, position.y - c.BULLET_RADIUS, c.BULLET_RADIUS * 2, c.BULLET_RADIUS * 2);
        shapeRenderer.end();
        update();
	}
	
	public Unit getUnit() {
		return unit;
	}
}
