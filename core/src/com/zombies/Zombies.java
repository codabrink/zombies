package com.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.util.Random;

public class Zombies extends Game {
    public static Zombies main;
    private Random random = new Random();
    private Box2DDebugRenderer renderer;
    public GameView view;
    public EndView endView;
    public PreView preView;

    @Override
    public void create() {
        main = this;
        setScreen(new PreView());
    }

    public void reset() {
        //view.meshes.main.stop();
        view.world.dispose();
        view = null;
        System.gc();
        setScreen(preView);
        System.gc();
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        if (view != null) view.setCamera(new PerspectiveCamera(67, 2f * aspectRatio, 2f));
        if (view != null) {
            view.getThumbpadLeft().updateResize();
        }
    }
}
