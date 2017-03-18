package com.zombies.util;

public class Bounds2 {
    public float x, y, w, h;

    public Bounds2() {
        x = 0; y = 0; w = 0; h = 0;
    }
    public Bounds2(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public static Bounds2 crop(Bounds2 b1, Bounds2 b2) {
        return crop(b1.x, b1.y, b1.w, b1.h, b2.x, b2.y, b2.w, b2.h);
    }
    public static Bounds2 crop(float x0, float y0, float w0, float h0, float x1, float y1, float w1, float h1) {
        Bounds2 bounds = new Bounds2();
        bounds.x = Math.max(x0, x1);
        bounds.y = Math.max(y0, y1);
        bounds.w = Math.min(w1, w0 - (x1 - x0));
        bounds.h = Math.min(h1, h0 - (y1 - y0));
        return bounds;
    }
}
