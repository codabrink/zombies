package com.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontGen {
    public static BitmapFont font12, font16, font24, font72;

    public FontGen() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myfont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font12 = generator.generateFont(parameter);
        parameter.size = 16;
        font16 = generator.generateFont(parameter);
        parameter.size = 24;
        font24 = generator.generateFont(parameter);
        parameter.size = 72;
        font72 = generator.generateFont(parameter);
        generator.dispose();
    }
}
