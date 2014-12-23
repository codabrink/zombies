package com.zombies;

public class HealthBar {

	private C c;
	private Player player;
	private float x, width, height;
	private boolean setUp = false;
	private GameView view;
	
	public HealthBar(GameView view) {
		this.view = view;
		
	}
	
	public void draw() {
		if (player.getHealthPercent() < 1f) {
			
		}
	}
	
	private void updateVerticies() {
		float alpha = (1.0f - player.getHealthPercent()) * 255f;
		float green = player.getHealthPercent() * 255f;
		float red = (1.0f - player.getHealthPercent()) * 255f;

	}
	
}
