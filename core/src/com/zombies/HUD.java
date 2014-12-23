package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HUD {

	GameView view;
	
	public HUD(GameView view) {
		this.view = view;
	}
	
	public void render(SpriteBatch spriteBatch) {
		view.getPlayer().renderGunInfo(spriteBatch);
		view.getThumbpad().render(spriteBatch);
		view.getShootButton().render(spriteBatch);
		this.drawHealth(spriteBatch);
		this.drawZombiesKilled(spriteBatch);
	}
	
	public void touch(float x, float y, int i) {
		view.getPlayer().handleTouch(x, y);
		//joystick
		if (x < view.getWidth() * 0.5f) {
			view.getThumbpad().updateFromTouch(Gdx.input.getX(), Gdx.input.getY(), i);
		}
		//shoot button
		if (y > view.getHeight() * 0.5f && x > view.getWidth() * 0.5f) {
			System.out.println("1");
			view.getShootButton().handleTouch(x, y, i);
		}
	}
	
	private void drawHealth(SpriteBatch spriteBatch) {
		spriteBatch.end();
		view.mh.sBatch.begin();
		view.mh.font.draw(view.mh.sBatch, "Health: " + String.valueOf((int)view.getPlayer().getHealth()), view.getWidth() - view.getWidth() / 2 - 40, 35);
		view.mh.sBatch.end();
		spriteBatch.begin();
	}
	
	private void drawZombiesKilled(SpriteBatch spriteBatch) {
		spriteBatch.end();
		view.mh.sBatch.begin();
		view.mh.font.draw(view.mh.sBatch, "Zombies Killed: " + String.valueOf((int)view.s.zombieKills), view.getWidth() - view.getWidth() / 2 - 40, 20);
		view.mh.sBatch.end();
		spriteBatch.begin();
	}
	
}
