package com.HUD;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.zombies.GameView;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by coda on 2/29/2016.
 */
public class DebugText {
    public static Hashtable<String,String> m = new Hashtable<String, String>();
    private static SpriteBatch spriteBatch = new SpriteBatch();

    public static void render() {
        GameView view = GameView.m;
        spriteBatch.begin();
        int i = 1;
        for (Map.Entry<String, String> e : m.entrySet()) {
            view.fontGen.font24.draw(spriteBatch, e.getValue(), 0, view.getHeight() - i * 20);
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
