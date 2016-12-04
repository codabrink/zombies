package com.zombies.HUD;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombies.Box;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Player;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.map.Hallway;
import com.zombies.map.MapGen;
import com.zombies.util.U;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {
    private GameView view;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch   spriteBatch;
    private int           fontSize = 18;
    private BitmapFont    font = FontGen.generateFont(fontSize, "serif-reg");
    final Pattern commandPattern =  Pattern.compile("/([a-zA-Z]+)\\s*([a-zA-Z0-9]+)?");

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
                string = "";
                break;
        }

        return true;
    }

    public boolean keyTyped(char c) {
        if (Character.toString(c).matches("[a-zA-Z0-9\\s/]"))
            string += c;
        return true;
    }

    private void submit() {
        Matcher m = commandPattern.matcher(string);
        string = "";

        if (!m.find())
            return;

        Player p;
        Box b;

        enabled = false;

        switch(m.group(1)) {
            case "boxmap":
                C.DEBUG_SHOW_BOXMAP = !C.DEBUG_SHOW_BOXMAP;
                break;
            case "inspectBox":
                p = view.getPlayer();
                b = Zone.getZone(p.getPosition()).getBox(p.getPosition());
                break;
            case "connectRoom":
                p = view.getPlayer();
                b = Zone.getZone(p.getPosition()).getBox(p.getPosition());
                MapGen.connectRoom(b.getRoom());
            case "hallway":
                p = view.getPlayer();
                b = Zone.getZone(p.getPosition()).getBox(p.getPosition());
                new Hallway(b, Integer.parseInt(m.group(2)), 1);
            default:
                enabled = true;
                return;
        }
    }

    public void draw() {
        float padding = view.getWidth() * 0.1f;
        BitmapFont font = Zombies.getFont("sans-reg:16:black");

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(200, 200, 200, 0.5f);
        shapeRenderer.rect(padding, padding, view.getWidth() - padding * 2, 20);
        shapeRenderer.end();

        spriteBatch.begin();
        font.draw(spriteBatch, string + '|', padding + 5, padding + 20 - 4);
        spriteBatch.end();
    }

    public boolean toggleEnabled() {
        return enabled = !enabled;
    }
}
