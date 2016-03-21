package com.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.util.Random;

public class Zombies extends Game {
    public static Zombies game;

    @Override
    public void create() {
        game = this;
        setScreen(new PreView());
    }

    @Override
    public void resize(int width, int height) {

    }
}
