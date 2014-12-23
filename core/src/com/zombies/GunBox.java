package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class GunBox {

	private GameView view;
	private Gun gun;
	private Texture texture;
	private Texture textureGreen;
	private float x;
	private float size;
	
	public GunBox(GameView view, Gun gun) {
		this.gun = gun;
		this.view = view;
		texture = new Texture(Gdx.files.internal("data/gunbox.png"));
		textureGreen = new Texture(Gdx.files.internal("data/gunbox-green.png"));
	}
	
	public void render(int num, SpriteBatch spriteBatch) {
		this.size = view.getWidth() * view.c.GUNBOX_WIDTH;
		this.x = view.getWidth() - size * num;
		float scale = size / texture.getWidth();
		if (view.getPlayer().getGun() == gun) {
			spriteBatch.draw(textureGreen, x, view.getHeight() - size, 0, 0, texture.getWidth(), texture.getHeight(), scale, scale, 0, 0, 0, texture.getHeight(), texture.getWidth(), false, false);
		} else {
			spriteBatch.draw(texture, x, view.getHeight() - size, 0, 0, texture.getWidth(), texture.getHeight(), scale, scale, 0, 0, 0, texture.getHeight(), texture.getWidth(), false, false);
		}
		Texture gunTexture = gun.getTexture();
		float gunSize = size * 0.8f;
		float gunScale = gunSize / gunTexture.getWidth();
		float gunX = x + size / 2f - gunSize / 2f;
		float gunY = view.getHeight() - gunTexture.getHeight() * gunScale;
		spriteBatch.draw(gun.getTexture(), gunX, gunY, 0, 0, gunTexture.getWidth(), gunTexture.getHeight(), gunScale, gunScale, 0, 0, 0, gunTexture.getWidth(), gunTexture.getHeight(),  false, false);
		spriteBatch.end();
		view.mh.sBatch.begin();
		view.mh.font.draw(view.mh.sBatch, String.valueOf(gun.getAmmo()), x + size / 2f - 10, view.getHeight() - size + 20);
		view.mh.sBatch.end();
		spriteBatch.begin();
	}
	
	public void handleTouch(float x, float y) {
		if (x > this.x && x < this.x + size) {
			if (y < size) {
				view.getPlayer().setGun(gun);
			}
		}
	}
	
}