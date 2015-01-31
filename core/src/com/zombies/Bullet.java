package com.zombies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class Bullet {
	private Unit unit;
	private long createTime = System.currentTimeMillis();
	private C c;
    private Vector2 position;
    private Vector2 direction;
    private String[] stoppingObjects = {"crate", "wall"};
	private GameView view;
	private float speed = 1f;
    private RayCastCallback callback;

	public Bullet(GameView view, Unit unit, short group, Vector2 position, Vector2 direction) {
		c = view.c;
		this.view = view;
        this.position = position;
        this.direction = direction.setLength(speed);
		this.unit = unit;

        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                return 0;
            }
        };

        view.getWorld().rayCast(callback, position, position.add(new Vector2(direction).setLength(100)));
        if (unit == view.getPlayer())
            System.out.println(position.toString());
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
