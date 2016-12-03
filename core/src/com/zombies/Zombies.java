package com.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.zombies.HUD.FontGen;

import java.util.HashMap;
public class Zombies extends Game {
    public static Zombies instance;
    public static HashMap<String, BitmapFont> fonts = new HashMap<>();

    @Override
    public void create() {
        instance = this;
        generateFonts();
        setScreen(new PreView());
    }

    private void generateFonts() {
        fonts.put("serif-reg:18:white", FontGen.generateFont(18, "serif-reg"));
        fonts.put("sans-reg:18:white",  FontGen.generateFont(18, "sans-reg"));
        fonts.put("sans-reg:18:black",  FontGen.generateFont(18, "sans-reg", Color.BLACK));
        fonts.put("sans-reg:24:white",  FontGen.generateFont(24, "sans-reg"));
        fonts.put("square:48:red",      FontGen.generateFont(48, "square", Color.RED));
    }

    @Override
    public void resize(int width, int height) {

    }
}
