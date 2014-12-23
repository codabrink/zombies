package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ShootButton {

	private GameView view;
	private float diameter;
	private Texture buttonDown = new Texture(Gdx.files.internal("data/button-d.png"));
	private Texture buttonUp = new Texture(Gdx.files.internal("data/button-u.png"));
	private int index = 0;
	boolean down = false;
	
	public ShootButton(GameView view) {
		this.view = view;
		
	}
	
	public void handleTouch(float x, float y, int i) {
		index = i;
		diameter = view.getWidth() * 0.2f;
		if (x > view.getWidth() - diameter) {
			if (y > view.getHeight() - diameter) {
				view.getPlayer().shoot(view.getPlayer().getDirection());
				down = true;
			}
		}
	}
	
	public void render(SpriteBatch sBatch) {
		diameter = view.getWidth() * 0.2f;
		if (down == true && Gdx.input.isTouched(index)) {
			sBatch.draw(buttonDown, view.getWidth() - diameter, 0, 0, 0, diameter, diameter, 1, 1, 0, 0, 0, buttonDown.getWidth(), buttonDown.getHeight(), false, false);
		} else {
			sBatch.draw(buttonUp, view.getWidth() - diameter, 0, 0, 0, diameter, diameter, 1, 1, 0, 0, 0, buttonDown.getWidth(), buttonDown.getHeight(), false, false);
			down = false;
		}
	}
	
}
