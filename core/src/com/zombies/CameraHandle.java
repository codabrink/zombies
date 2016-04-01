package com.zombies;

import com.badlogic.gdx.graphics.PerspectiveCamera;

public class CameraHandle {
    private GameView view;
    private Player player;
    private PerspectiveCamera cam;

    private float zoom = 100;
    private float lerp = 5f;

    public CameraHandle(GameView view) {
        this.view = view;
        this.player = view.getPlayer();
    }

    public void update(float dt) {
        cam = view.getCamera();

        float dest = view.getPlayer().getBody().getLinearVelocity().len() * 4;
        if (zoom < dest)
            zoom += lerp * dt;
        else if (zoom > dest && zoom > 40)
            zoom -= lerp * dt;

        cam.position.set(player.getBody().getPosition().x, player.getBody().getPosition().y, cam.position.z);
        cam.update();
    }
}
