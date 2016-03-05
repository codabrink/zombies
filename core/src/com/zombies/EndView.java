package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EndView implements Screen {

    private SpriteBatch spriteBatch;
    protected Zombies main;
    private long startTime = System.currentTimeMillis();

    public EndView() {
        this.main = Zombies.main;
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        spriteBatch.enableBlending();
        spriteBatch.end();
        main.view.mh.sBatch.begin();
        main.view.mh.font.draw(main.view.mh.sBatch, "Zombie Kills: " + String.valueOf(main.view.s.zombieKills), 100, getHeight() / 2 + 10);
        main.view.mh.font.draw(main.view.mh.sBatch, "Touch screen to play again.", 100, getHeight() / 2 - 10);
        main.view.mh.sBatch.end();
        spriteBatch.begin();
        spriteBatch.end();
        Gdx.gl.glFlush();
        handleKeys();
    }

    private void handleKeys() {
        for (int i=0; i<3; i++) {
            if (Gdx.input.isTouched(i) && System.currentTimeMillis() > startTime + 500l) {
                System.gc();
                main.setScreen(new GameView());
                System.gc();
            }
        }
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resize(int arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

}
