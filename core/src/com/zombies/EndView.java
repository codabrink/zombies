package com.zombies;

import com.zombies.HUD.FontGen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zombies.data.Stats;

public class EndView implements Screen {

    private SpriteBatch spriteBatch;
    private long startTime = System.currentTimeMillis();
    private Stats stats;

    public EndView(Stats stats) {
        this.stats = stats;
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        FontGen.font24.draw(spriteBatch, "Zombie Kills: " + String.valueOf(stats.zombieKills), 100, getHeight() / 2 + 10);
        FontGen.font24.draw(spriteBatch, "Touch screen to play again.", 100, getHeight() / 2 - 10);
        spriteBatch.end();
        Gdx.gl.glFlush();
        handleKeys();
    }

    private void handleKeys() {
        for (int i=0; i<3; i++) {
            if (Gdx.input.isTouched(i) && System.currentTimeMillis() > startTime + 500l) {
                System.gc();
                Zombies.game.setScreen(new GameView());
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
