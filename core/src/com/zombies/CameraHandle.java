package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.zombies.data.D;

public class CameraHandle {
    private GameView view;
    private Player player;
    public PerspectiveCamera cam;

    float lerp = 1f;
    public float z = 100f;

    public CameraHandle(GameView view) {
        cam = new PerspectiveCamera(C.FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.view = view;
        this.player = view.getPlayer();
    }

    public void resize(int width, int height) {
        cam = new PerspectiveCamera(C.FOV, width, height);
    }

    public void update(float dt) {
        z = C.DRAW_DISTANCE * 100 + 150;

        Vector3 position = cam.position;
        cam.far = z + 200;
        cam.fieldOfView = C.FOV;
        position.x += (D.player().getPosition().x - position.x) * lerp * dt;
        position.y += (D.player().getPosition().y - position.y) * lerp * dt;
        position.z += (z - position.z) * lerp * dt;
        cam.update();
    }
}
