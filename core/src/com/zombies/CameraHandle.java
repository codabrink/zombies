package com.zombies;

import com.badlogic.gdx.graphics.PerspectiveCamera;

public class CameraHandle {
    private GameView view;
    private Player player;
    private PerspectiveCamera cam;

    private float zoom = 50 * C.scale;

    public CameraHandle(GameView view) {
        this.view = view;
        this.player = view.getPlayer();
    }

    public void update(float dt) {
        cam = view.getCamera();
        cam.position.set(player.getBody().getPosition().x, player.getBody().getPosition().y, zoom);
        cam.update();
    }
}
