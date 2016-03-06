package com.HUD;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zombies.GameView;

public class HUD implements InputProcessor{

	GameView view;
    private String debugMessage = "";
	
	public HUD() {
		this.view = GameView.gv;
	}

    public void update() {
        view.getThumbpadLeft().update();
    }

	public void render(SpriteBatch spriteBatch) {
		view.getPlayer().renderGunInfo(spriteBatch);
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
            view.getThumbpadLeft().render(spriteBatch);
            view.getThumbpadRight().render(spriteBatch);
        }
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

    private void drawZombiesKilled(SpriteBatch spriteBatch) {
        view.fontGen.killFont.draw(spriteBatch, String.valueOf(view.stats.zombieKills), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 10);
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
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
            //Left thumbpad
            if (screenX < Gdx.graphics.getWidth() * 0.5f) {
                view.getThumbpadLeft().updateFromTouch(screenX, screenY, pointer);
            }
            //Right thumbpad
            if (screenX > Gdx.graphics.getWidth() * 0.5f) {
                view.getThumbpadRight().updateFromTouch(screenX, screenY, pointer);
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
            view.getThumbpadLeft().touchUp(screenX, screenY, pointer);
            view.getThumbpadRight().touchUp(screenX, screenY, pointer);
        }
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
