package com.zombies;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ThumbpadLeft extends Thumbpad {
    private Texture thumbPoint = new Texture(Gdx.files.internal("data/joy-p.png"));
    private Texture thumbArea = new Texture(Gdx.files.internal("data/joy-a.png"));
    private Vector2 center;
    private GameView view;
    private Vector2 touch;
    private float radius;
    private int index = 0;
    private boolean down = false;

    public ThumbpadLeft(GameView view) {
        this.view = view;
        center = new Vector2();
        touch = new Vector2(1, 1);
        radius = 0;
        updateResize();
    }

    public Vector2 getTouch() {
        return touch;
    }

    public void update() {
        if (down && !view.c.ENABLE_ACCEL) {
            view.player.getBody().applyForce(touch, new Vector2(), true);
        }
    }

    public void updateResize() {
        radius = view.getWidth() * 0.15f;
        center = new Vector2(radius, radius);
    }

    public void updateFromTouch(float x, float y, int i) {
        touch = new Vector2(x, view.getHeight() - y).sub(center);
        if (touch.len() > radius * 0.8f) {
            touch = touch.setLength(radius * 0.8f);
        }
        down = true;
        index = i;
    }

    public void updateFromDrag(float x, float y, int i) {
        touch = new Vector2(x, view.getHeight() - y).sub(center);
        if (touch.len() > radius * 0.8f) {
            touch = touch.setLength(radius * 0.8f);
        }
    }

    public void touchUp(float x, float y, int i) {
        if (i == index) {
            touch = new Vector2(0, 0);
            down = false;
        }
    }

    public void render(SpriteBatch sBatch) {
        if (down == true && !Gdx.input.isTouched(index)) {
            touch = new Vector2(1,0);
            down = false;
        }
        float areaSize = radius * 2f;
        sBatch.draw(thumbArea, center.x - areaSize / 2f, center.y - areaSize / 2f, 0, 0, areaSize, areaSize, 1, 1, 0, 0, 0, thumbArea.getWidth(), thumbArea.getHeight(), false, false);

        float pointSize = radius;
        sBatch.draw(thumbPoint, touch.x + center.x - radius / 2f, touch.y + center.y - radius / 2f, 0, 0, pointSize, pointSize, 1, 1, 0, 0, 0, thumbPoint.getWidth(), thumbPoint.getHeight(), false, false);
   }
   
}