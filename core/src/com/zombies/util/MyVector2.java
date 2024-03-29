package com.zombies.util;

import com.badlogic.gdx.math.Vector2;

public class MyVector2 extends Vector2 {
    private float angle, length;

    public MyVector2(float x, float y, float length, float angle) {
        super(x, y);
        this.length = length;
        this.angle = angle;
    }
    public MyVector2(Vector2 v, float length, float angle) {
        super(v.x, v.y);
        this.length = length;
        this.angle = angle;
    }

    public Vector2 end() {
        //System.out.println((float)(len() * Math.cos(getAngle * (Math.PI/180))));
        //System.out.println((float)(len() * Math.sin(getAngle * (Math.PI/180))));
        return cpy().add((float)(len() * Math.cos(angle * (Math.PI/180))), (float)(len() * Math.sin(angle * (Math.PI/180))));
    }

    // add a given dst on to the position magnitude of this vector. return the result as an ordinary vector.
    public Vector2 project(float dst) {
        return cpy().add((float)(dst * Math.cos(angle * (Math.PI/180))), (float)(dst * Math.sin(angle * (Math.PI/180))));
    }

    public float angle() {
        return angle;
    }
    @Override
    public Vector2 setAngle(float angle) {
        this.angle = angle;
        return this;
    }
    @Override
    public float len() {
        return length;
    }
    public MyVector2 mycpy() {
        return new MyVector2(x, y, length, angle);
    }

}
