package com.zombies.HUD;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombies.GameView;
import com.zombies.Zombies;

public class Console {
    private GameView view;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch   spriteBatch;
    private int           fontSize = 18;
    private BitmapFont    font = FontGen.generateFont(fontSize, "serif-reg");

    public boolean enabled = false;
    private String  string  = "";

    public Console() {
        view = GameView.gv;
        shapeRenderer = new ShapeRenderer();
        spriteBatch   = new SpriteBatch();
    }

    public boolean keyDown(int keycode) {
        switch(keycode) {
            case 66: // enter
                submit();
                break;
            case 67: // backspace
                if (string != null && string.length() > 0) {
                    string = string.substring(0, string.length()-1);
                }
                break;
            case 68: // backtick (disable)
                enabled = false;
                break;
        }

        // System.out.println(keycode);
        return true;
    }

    public boolean keyTyped(char c) {
        if (Character.toString(c).matches("[a-zA-Z\\s]"))
            string += c;
        return true;
    }

    private void submit() {
        switch(string) {

        }
    }

    public void draw() {
        float padding = view.getWidth() * 0.1f;
        BitmapFont font = Zombies.fonts.get("sans-reg:18:black");

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(200, 200, 200, 0.5f);
        shapeRenderer.rect(padding, padding, view.getWidth() - padding * 2, 20);
        shapeRenderer.end();

        spriteBatch.begin();
        font.draw(spriteBatch, string, padding + 5, padding + 20 - 4);
        spriteBatch.end();
    }

    public boolean toggleEnabled() {
        return enabled = !enabled;
    }
}
