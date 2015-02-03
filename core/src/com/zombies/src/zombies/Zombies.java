package com.zombies.src.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.util.Random;

public class Zombies extends Game {
    private Random random = new Random();
    private Box2DDebugRenderer renderer;
    public GameView view;
    public EndView endView;
    public PreView preView;
    private int width, height;
    public boolean resetFlat = false;
    private long startTime = System.currentTimeMillis();
    private long[] touch = new long[4];


    @Override
    public void create() {
        view = new GameView(this);
        preView = new PreView(this);
        setScreen(preView);
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
        this.width = width;
        this.height = height;
        if (view != null) {
            view.getThumbpadLeft().updateResize();
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
