package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.map.building.Building;

public class House extends Building {
    private int roomCount;
    private float margin;

    public House(Vector2 c, int roomCount, float margin) {
        this.roomCount = roomCount;
        this.margin    = margin;

        generate();
    }

    private void generate() {

    }
}
