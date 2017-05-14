package com.zombies.map.building.window;

import com.badlogic.gdx.math.Vector2;
import com.zombies.map.building.Box;
import com.zombies.map.building.Building;

public class WindowContainer {
    private WindowFrame windowFrame;

    public WindowContainer(Vector2 p1, Vector2 p2, Building b) {
        windowFrame = new WindowFrame(p1, p2, b);
    }
}
