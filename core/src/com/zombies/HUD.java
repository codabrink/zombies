package com.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HUD implements InputProcessor{

	GameView view;
    private String debugMessage = "";
	
	public HUD(GameView view) {
		this.view = view;
	}

    public void update() {
        view.getThumbpadLeft().update();
    }

	public void render(SpriteBatch spriteBatch) {
		view.getPlayer().renderGunInfo(spriteBatch);
		view.getThumbpadLeft().render(spriteBatch);
        view.getThumbpadRight().render(spriteBatch);
		this.drawHealth(spriteBatch);
		this.drawZombiesKilled(spriteBatch);
        this.drawDebug();
	}

    public void setDebugMessage(String message) {debugMessage = message;}

    private void drawDebug() {
        view.getHUDSpriteBatch().end();
        view.mh.sBatch.begin();
        view.mh.font.draw(view.mh.sBatch, debugMessage, 20, view.getHeight() - 40);
        view.mh.sBatch.end();
        view.getHUDSpriteBatch().begin();
    }

	private void drawHealth(SpriteBatch spriteBatch) {
		spriteBatch.end();
		view.mh.sBatch.begin();
		view.mh.font.draw(view.mh.sBatch, "Health: " + String.valueOf((int)view.getPlayer().getHealth()), view.getWidth() - view.getWidth() / 2 - 40, 35);
		view.mh.sBatch.end();
		spriteBatch.begin();
	}
	
	private void drawZombiesKilled(SpriteBatch spriteBatch) {
        if (true) return;
		spriteBatch.end();
		view.mh.sBatch.begin();
        view.mh.font.setScale(2);
		view.mh.font.draw(view.mh.sBatch, "Zombies Killed: " + String.valueOf((int)view.s.zombieKills), view.getWidth() - view.getWidth() / 2 - 40, 20);
		view.mh.sBatch.end();
		spriteBatch.begin();
	}

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //Left thumbpad
        if (screenX < Gdx.graphics.getWidth() * 0.5f) {
            view.getThumbpadLeft().updateFromTouch(screenX, screenY, pointer);
        }
        //Right thumbpad
        if (screenX > Gdx.graphics.getWidth() * 0.5f) {
            view.getThumbpadRight().updateFromTouch(screenX, screenY, pointer);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        view.getThumbpadLeft().touchUp(screenX, screenY, pointer);
        view.getThumbpadRight().touchUp(screenX, screenY, pointer);
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int i) {
        //Left thumbpad
        if (x < view.getWidth() * 0.5f) {
            view.getThumbpadLeft().updateFromDrag(x, y, i);
        }
        //Right thumbpad
        if (x > Gdx.graphics.getWidth() * 0.5f) {
            view.getThumbpadRight().updateFromDrag(x, y, i);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
