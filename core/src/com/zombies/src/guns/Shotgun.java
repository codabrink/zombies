package com.zombies.src.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.zombies.src.zombies.Bullet;
import com.zombies.src.zombies.GameView;
import com.zombies.src.zombies.Gun;
import com.zombies.src.zombies.Message;
import com.zombies.src.zombies.Unit;

import java.util.ArrayList;
import java.util.Random;

public class Shotgun extends Gun {

	private int shotSize = 6;
	private Random random = new Random();
	private float shotSpread = 5;
	private Sound shoot = Gdx.audio.newSound(Gdx.files.internal("data/sound/shotgun.mp3"));
    private Random r = new Random();
	
	public Shotgun(GameView view, Unit unit, int ammo) {
		super(view, unit);
		speed = c.SHOTGUN_SPEED;
		type = "shotgun";
		this.ammo = ammo;
		this.texture = view.getMeshes().shotgunTexture;
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
		if (ammo <= 0) {
			view.mh.addMessage(new Message(view, "You're out of ammo! Find another gun!"));
			lastShot = System.currentTimeMillis();
			return;
		}
		lastShot = System.currentTimeMillis();
		for (int i=1; i <= shotSize; i++) {
			Vector2 tempDirection = direction.rotate(random.nextFloat() * shotSpread * 2 - shotSpread);
            Bullet bullet = new Bullet(view, unit, unit.getGroup(), this, unit.getBody().getPosition(), tempDirection);
			bullets.add(bullet);

            ArrayList<Unit> killRoster = bullet.unitsInBulletRange();
            if (killRoster.size() > 0)
                killRoster.get(r.nextInt(killRoster.size())).die(unit);
		}
		unit.getBox().getRoom().alarm(unit);
		view.s.shots ++;
		ammo--;
		shoot.play();
	}
	
	@Override
	public void update() {
		super.update();
	}
	
}