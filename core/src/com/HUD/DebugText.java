package com.HUD;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zombies.GameView;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by coda on 2/29/2016.
 */
public class DebugText {
    public static Hashtable<String,String> m = new Hashtable<String, String>();

    public static void render() {
        GameView view = GameView.m;
        view.mh.sBatch.begin();
        int i = 1;
        for (Map.Entry<String, String> e : m.entrySet()) {
            view.mh.font.draw(view.mh.sBatch, e.getValue(), 0, view.getHeight() - i * 20);
            i++;
        }
        view.mh.sBatch.end();

    }
    public static void clearMessages() {
        m = new Hashtable<String, String>();
    }
    public static void addMessage(String k, String v) {
        m.put(k, v);
    }
}
