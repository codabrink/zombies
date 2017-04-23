package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.zombies.data.D;

public class CameraHandle {
    private GameView view;
    private Player player;
    public PerspectiveCamera cam;

    float lerp = 5f;
    public static float z = 50f;

    private boolean inBuilding = false;

    public CameraHandle(GameView view) {
        cam = new PerspectiveCamera(C.FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.view = view;
        this.player = view.getPlayer();
    }

    public void resize(int width, int height) {
        cam = new PerspectiveCamera(C.FOV, width, height);
    }

    public void update(float dt) {
        Vector3 position = cam.position;

        if (D.currentBox != null) {
            z = 20;
        } else if (D.currentBox == null) {
            z = 60;
        }

        cam.far = position.z + 100;
        cam.fieldOfView = C.FOV;
        position.x += (D.player().getPosition().x - position.x) * lerp * dt;
        position.y += (D.player().getPosition().y - position.y) * lerp * dt;
        position.z += (z - position.z) * lerp * dt;
        cam.update();
    }
}
