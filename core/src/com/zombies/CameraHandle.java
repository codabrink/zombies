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
	
	public CameraHandle(GameView view) {
		this.view = view;
		this.player = view.getPlayer();
		this.main = view.getMain();
	}
	
	public void update() {
		cam = view.getCamera();
		cam.position.set(20, 20, 20);
		movement.mul(new Matrix3());
	}
}
