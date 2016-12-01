package com.zombies.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontGen {
    public static BitmapFont font12, font16, font24, font72;
    public static BitmapFont killFont;

    public FontGen() {
        float density = Gdx.graphics.getDensity();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/sans-reg.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(density * 24);
        font24 = generator.generateFont(parameter);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/square.ttf"));
        parameter.size = (int)(density * 48);
        parameter.color = Color.RED;
        killFont = generator.generateFont(parameter);

        generator.dispose();
    }

    public static BitmapFont generateFont(int size, String font, Color color) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + font + ".ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = color;
        parameter.size  = (int)(Gdx.graphics.getDensity() * size);
        return generator.generateFont(parameter);
    }

    public static BitmapFont generateFont(int size, String font) {
        return generateFont(size, font, Color.WHITE);
    }

    public static BitmapFont generateFont(int size, String font, Color color, Color borderColor) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + font + ".ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(Gdx.graphics.getDensity() * size);
        parameter.color = color;
        parameter.borderWidth = 1;
        parameter.borderColor = borderColor;
        return generator.generateFont(parameter);
    }
}
