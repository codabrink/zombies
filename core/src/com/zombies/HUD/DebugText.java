package com.zombies.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombies;

import java.util.Hashtable;
import java.util.Map;

public class DebugText {
    public static Hashtable<String,String> m = new Hashtable<String, String>();
    private static SpriteBatch spriteBatch = new SpriteBatch();

    public static void render() {
        if (!C.DEBUG)
            return;
        GameView view = GameView.gv;
        spriteBatch.begin();
        int i = 1;
        for (Map.Entry<String, String> e : m.entrySet()) {
            Zombies.fonts.get("sans-reg:24:white").draw(spriteBatch, e.getValue(), 0, view.getHeight() - i * 20 * Gdx.graphics.getDensity());
            i++;
        }
        spriteBatch.end();
    }
    public static void clearMessages() {
        m = new Hashtable<String, String>();
    }
    public static void addMessage(String k, String v) {
        m.put(k, v);
    }
}
