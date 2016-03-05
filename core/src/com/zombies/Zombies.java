package com.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.util.Random;

public class Zombies extends Game {
    public static Zombies main;
    private Random random = new Random();
    private Box2DDebugRenderer renderer;
    public GameView view;

    @Override
    public void create() {
        main = this;
        setScreen(new PreView());
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        if (view != null) view.setCamera(new PerspectiveCamera(67, 2f * aspectRatio, 2f));
        if (view != null)
            view.getThumbpadLeft().updateResize();
    }
}
