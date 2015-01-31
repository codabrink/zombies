package com.guns;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombies.Bullet;
import com.zombies.GameView;
import com.zombies.Gun;
import com.zombies.Message;
import com.zombies.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Shotgun extends Gun {

	private int shotSize = 4;
	private Random random = new Random();
	private float shotSpread = 10;
	private Sound shoot = Gdx.audio.newSound(Gdx.files.internal("data/sound/shotgun.mp3"));
	
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
			bullets.add(new Bullet(view, unit, unit.getGroup(), this, unit.getBody().getPosition(), tempDirection));
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