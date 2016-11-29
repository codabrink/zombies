package com.zombies.HUD;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombies.C;
import com.zombies.GameView;

public class HealthBar {
	private GameView view;
	private ShapeRenderer shapeRenderer;

	public HealthBar() {
        view = GameView.gv;
		shapeRenderer = new ShapeRenderer();
	}
	
	public void draw() {
        float p = (view.player.getHealth() / C.PLAYER_HEALTH);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0.3f, 0, 1);
        shapeRenderer.rect(0, 0, view.getWidth(), view.getHeight() * 0.03f);
        shapeRenderer.setColor(1-p, p * 0.9f, 0, 1);
        shapeRenderer.rect(0, 0, view.getWidth() * p, view.getHeight() * 0.03f);
        shapeRenderer.end();
	}
}
