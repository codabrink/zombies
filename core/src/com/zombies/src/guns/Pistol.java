package com.zombies.src.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.zombies.src.zombies.Bullet;
import com.zombies.src.zombies.GameView;
import com.zombies.src.zombies.Gun;
import com.zombies.src.zombies.Message;
import com.zombies.src.zombies.Unit;

import java.util.ArrayList;
import java.util.Random;

public class Pistol extends Gun {
	private Sound shoot = Gdx.audio.newSound(Gdx.files.internal("data/sound/pistol.mp3"));
	private RayCastCallback callback;
    private Random r = new Random();

	public Pistol(GameView view, Unit unit, int ammo) {
		super(view, unit);
		speed = 200l;
		type = "pistol";
		this.ammo = ammo;
		this.texture = view.getMeshes().pistolTexture;

        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                return 1;
            }
        };
	}

	@Override
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
		for (Bullet b: bullets) {
			b.draw(spriteBatch, shapeRenderer);
		}
	}
	
	@Override
	public void shoot(Vector2 direction) {
		if (System.currentTimeMillis() < lastShot + speed) { return; }
		if (ammo == 0) {
			view.mh.addMessage(new Message(view, "You're out of ammo! Find another gun!"));
			lastShot = System.currentTimeMillis();
			return;
		}
		lastShot = System.currentTimeMillis();
        Bullet bullet = new Bullet(view, unit, unit.getGroup(), this, unit.getBody().getPosition(), direction);
        bullets.add(bullet);

        ArrayList<Unit> killRoster = bullet.unitsInBulletRange();
        if (killRoster.size() > 0)
            killRoster.get(r.nextInt(killRoster.size())).die(unit);

		unit.getBox().getRoom().alarm(unit);
		view.s.shots ++;
		if (ammo > 0) ammo--;
		shoot.play(0.2f);
	}
	
	@Override
	public void update() {
		super.update();
	}
	
}
