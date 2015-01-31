package com.zombies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import java.util.Arrays;

public class Bullet {
	private Unit unit;
	private long createTime = System.currentTimeMillis();
	private C c;
    private Vector2 position;
    private Vector2 direction;
    private String[] stoppingObjects = {"crate", "wall"};
	private GameView view;
	private float speed = 3f;
    private RayCastCallback callback;

	public Bullet(final GameView view, Unit unit, short group, Vector2 position, Vector2 direction) {
		c = view.c;
		this.view = view;
        this.position = position.cpy();
        this.direction = direction.cpy().setLength(speed);
		this.unit = unit;

        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                // If the fixture is in the stoppingObjects list
                BodData bodData = ((BodData)fixture.getBody().getUserData());
                if (bodData != null && Arrays.asList(stoppingObjects).contains(bodData.getType())) {
                    view.getHUD().setDebugMessage(normal.toString());
                }
                return 1;
            }
        };

        view.getWorld().rayCast(callback, position.cpy().add(direction.cpy().setLength(c.PLAYER_SIZE)), position.cpy().add(direction.cpy().setLength(100)));
	}

    public void update() {
        view.getHUD().setDebugMessage(direction.toString());
        position.add(direction);
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
