package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class CameraHandle {
    private GameView view;
    private Player player;
    public PerspectiveCamera cam;

    private float zoom = 50 * C.SCALE;

    public CameraHandle(GameView view) {
        cam = new PerspectiveCamera(C.FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.view = view;
        this.player = view.getPlayer();
    }

    public void resize(int width, int height) {
        cam = new PerspectiveCamera(C.FOV, width, height);
    }

    public void update(float dt) {
        cam.far = 300f;
        cam.fieldOfView = 90f;
        cam.position.set(player.getBody().getPosition().x, player.getBody().getPosition().y, zoom);
        cam.update();
    }
}
