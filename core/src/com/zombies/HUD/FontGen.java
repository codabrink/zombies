package com.zombies.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontGen {
    public static BitmapFont generateFont(int size, String fontName, Color color) {
        BitmapFont font;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + fontName + ".ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = color;
        parameter.size  = (int)(Gdx.graphics.getDensity() * size);

        font = generator.generateFont(parameter);
        generator.dispose();

        return font;
    }

    public static BitmapFont generateFont(int size, String font) {
        return generateFont(size, font, Color.WHITE);
    }

    public static BitmapFont generateFont(int size, String fontName, Color color, Color borderColor) {
        BitmapFont font;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + fontName + ".ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(Gdx.graphics.getDensity() * size);
        parameter.color = color;
        parameter.borderWidth = 1;
        parameter.borderColor = borderColor;

        font = generator.generateFont(parameter);
        generator.dispose();

        return font;
    }
}
