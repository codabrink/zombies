package com.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by coda on 3/31/2016.
 */
public interface Renderable {
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer);
}
