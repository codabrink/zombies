package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Coda on 1/29/15.
 */
public class ThumbpadRight extends Thumbpad {
    private Texture thumbPoint = new Texture(Gdx.files.internal("data/joy-p.png"));
    private Texture thumbArea = new Texture(Gdx.files.internal("data/joy-a.png"));
    private Vector2 center;
    private GameView view;
    private Vector2 touch;
    private float radius;
    private int index = 0;
    private boolean down = false;
    private long touchDown = 0;

    public ThumbpadRight(GameView view) {
        this.view = view;
        center = new Vector2();
        touch = new Vector2(1, 1);
        radius = 0;
        updateResize();
    }

    public void updateResize() {
        radius = Gdx.graphics.getWidth() * 0.15f;
        center = new Vector2(Gdx.graphics.getWidth() - radius, radius);
    }

    public void updateFromTouch(float x, float y, int i) {
        touchDown = System.currentTimeMillis();
    }

    public void updateFromDrag(float x, float y, int i) {
        touch = new Vector2(x, view.getHeight() - y).sub(center);
        if (touch.len() > radius * 0.8f) {
            touch = touch.setLength(radius * 0.8f);
        }
        view.player.setAngle((float) Math.toDegrees(Math.atan2(touch.y, touch.x)));
        index = i;
    }

    public void touchUp(float x, float y, int i) {
        if (i == index) {
            if (System.currentTimeMillis() - touchDown < 200) {
                view.getPlayer().shoot(view.getPlayer().getDirection());
            }
            touch = new Vector2(1, 0);
        }
    }

    public void render(SpriteBatch sBatch) {
        float areaSize = radius * 2f;
        sBatch.draw(thumbArea, center.x - areaSize / 2f, center.y - areaSize / 2f, 0, 0, areaSize, areaSize, 1, 1, 0, 0, 0, thumbArea.getWidth(), thumbArea.getHeight(), false, false);
        float pointSize = radius;
        sBatch.draw(thumbPoint, touch.x + center.x - radius / 2f, touch.y + center.y - radius / 2f, 0, 0, pointSize, pointSize, 1, 1, 0, 0, 0, thumbPoint.getWidth(), thumbPoint.getHeight(), false, false);
    }

    public Vector2 getTouch() {return touch;}
}
