package com.zombies;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

public class CameraHandle {
	private GameView view;
	private Player player;
	private Zombies main;
	private PerspectiveCamera cam;
	private Vector2 movement = new Vector2();

	private float zoom = 30;
    private float lerp = 5f;

	public CameraHandle(GameView view) {
		this.view = view;
		this.player = view.getPlayer();
		this.main = view.getMain();
	}
	
	public void update(float dt) {
		cam = view.getCamera();

        float dest = view.getPlayer().getBody().getLinearVelocity().len() * 4;
        if (zoom < dest)
            zoom += lerp * dt;
        else if (zoom > dest && zoom > 30)
            zoom -= lerp * dt;

		cam.position.set(player.getBody().getPosition().x, player.getBody().getPosition().y, zoom);
        cam.update();
		movement.mul(new Matrix3());
	}
}
