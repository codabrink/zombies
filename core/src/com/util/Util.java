package com.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.math.Vector2;

import javax.naming.Context;

public class Util {
    public static Vector2 endOfV2(Vector2 v, float angle) {
        return v.cpy().add((float)(v.len() * Math.cos(angle  * (Math.PI/180))), (float)(v.len() * Math.sin(angle * (Math.PI/180))));
    }
}
