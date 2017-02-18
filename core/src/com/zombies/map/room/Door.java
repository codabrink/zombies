package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Modelable;

public class Door {
    private Vector2 p1, p2;
    private Modelable modelable;

    public Door(Vector2 p1, Vector2 p2, Modelable m) {
        this.p1 = p1;
        this.p2 = p2;
        modelable = m;
    }
}
