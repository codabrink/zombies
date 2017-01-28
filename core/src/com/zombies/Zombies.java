package com.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.zombies.HUD.FontGen;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

public class Zombies extends Game {
    public static Zombies instance;
    public static HashMap<String, BitmapFont> fonts = new HashMap<>();
    public static HashSet<Thread> threads = new HashSet<>();

    @Override
    public void create() {
        instance = this;
        generateFonts();
        setScreen(new PreView());
    }

    public static BitmapFont getFont(String fontKey) {
        BitmapFont font = fonts.get(fontKey);
        if (font != null) return font;

        String[] parts = fontKey.split(":");
        if (parts.length != 3) throw new IllegalArgumentException();

        Color c = Color.WHITE;
        try {
            Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(parts[2].toUpperCase());
            c = (Color)field.get(null);
        } catch(Exception e ) { }

        font = FontGen.generateFont(Integer.parseInt(parts[1]), parts[0], c);
        fonts.put(fontKey, font);
        return font;
    }

    private void generateFonts() {
        //getFont("serif-reg:18:white");
        //getFont("sans-reg:18:white");
        //getFont("sans-reg:18:black");
        //getFont("sans-reg:24:white");
        //getFont("square:48:red");
    }

    @Override
    public void resize(int width, int height) {

    }
}
