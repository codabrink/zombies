package com.zombies.HUD;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zombies;
import com.zombies.map.MapGen;
import com.zombies.map.room.Box;
import com.zombies.GameView;
import com.zombies.Zone;

public class HUD implements InputProcessor{

	private GameView view;
    private Console console = new Console();

    private String debugMessage = "";

	public HUD() {
		this.view = GameView.gv;
	}

    public void update() {
        view.getThumbpadLeft().update();
        handleKeys();
    }

	public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
		view.getPlayer().renderGunInfo(spriteBatch);
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
            view.getThumbpadLeft().render(spriteBatch);
            view.getThumbpadRight().render(spriteBatch);
        }
		this.drawZombiesKilled(spriteBatch);
        this.drawDebug();

        if (console.enabled)
            console.draw();
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
        Zombies.getFont("square:48:red").draw(spriteBatch, String.valueOf(view.stats.zombieKills), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 10);
	}

    private void handleKeys() {
        if (console.enabled)
            return;

        float strength = 2000 * C.SCALE;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            view.getPlayer().getBody().applyForce(new Vector2(0, strength), new Vector2(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            view.getPlayer().getBody().applyForce(new Vector2(0, -strength), new Vector2(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            view.getPlayer().getBody().applyForce(new Vector2(-strength, 0), new Vector2(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            view.getPlayer().getBody().applyForce(new Vector2(strength, 0), new Vector2(), true);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (console.enabled)
            return console.keyDown(keycode);

        Box b;
        switch(keycode) {
            case 43: // o
                b = Zone.getZone(GameView.gv.getPlayer().getPosition()).getBox(GameView.gv.getPlayer().getPosition());
                String out = "";
                for (int direction : MapGen.DIRECTIONS) {
                    if (b != null) {
                        Box bb = b.getAdjBox(direction);
                        out += "direction: " + direction + ": ";
                        if (bb == null)
                            out += "NULL, ";
                        else
                            out += bb + ", ";
                    }
                }
                break;
            case 36: // h - generate hallway in current box
                b = Zone.getZone(GameView.gv.getPlayer().getPosition()).getBox(GameView.gv.getPlayer().getPosition());
                if (b != null)
                    MapGen.genHallway(b);
                break;
            case 68: // ` - toggle console
                console.toggleEnabled();
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (console.enabled)
            return console.keyTyped(character);
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
        PerspectiveCamera c = view.getCamera();
        c.position.set(c.position.x, c.position.y, c.position.z + amount * 2);
        return true;
    }
}
