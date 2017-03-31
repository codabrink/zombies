package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class PreView implements Screen {

    private SpriteBatch spriteBatch;

    protected long startTime = System.currentTimeMillis();
    private int fontSize = 18;
    protected BitmapFont font = Zombies.getFont("serif-reg:" + fontSize + ":white");
    protected BitmapFont logoFont = Zombies.getFont("serif-reg:36:red");
    protected ArrayList<String> intro = new ArrayList<String>();
    protected float textStartHeight;

    public PreView() {
        spriteBatch = new SpriteBatch();

        intro.add("Blah blah blah.. Nothing to see here.");

        textStartHeight = Gdx.graphics.getHeight() / 2 + (intro.size() * fontSize * Gdx.graphics.getDensity());
    }

    @Override
    public void render(float arg0) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        logoFont.draw(spriteBatch, "Zombie Surge", 10, Gdx.graphics.getHeight() - 10);
        int i = 0;
        for(String s : intro) {
            i++;
            font.draw(spriteBatch, s, 10, textStartHeight - i * fontSize * Gdx.graphics.getDensity());
        }
        spriteBatch.end();

        Gdx.gl.glFlush();
        handleKeys();
    }

    private void handleKeys() {
        for (int i=0; i<3; i++) {
            if (Gdx.input.isTouched(i) && System.currentTimeMillis() > startTime + 500l) {
                System.gc();
                Zombies.instance.setScreen(new GameView());
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
