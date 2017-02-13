package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;

import java.util.HashMap;
import java.util.HashSet;

import static java.lang.Integer.parseInt;

public class Building {
    public static final int[] DIRECTIONS = {0, 90, 180, 270};

    public boolean threadLocked = false;
    private HashSet<Room> rooms = new HashSet<>();
    public HashMap<String, Box> boxMap = new HashMap<>();
    private Vector2 center;

    public Building(Vector2 center) {
        this.center = center;
    }

    public void refresh() {
        for (Box b : boxMap.values()) {
            rooms.add(b.getRoom());
        }
    }
    public Vector2 positionOf(String key) {
        String[] parts = key.split(",");
        return positionOf(parseInt(parts[0]), parseInt(parts[1]));
    }
    public Vector2 positionOf(int x, int y) {
        float vx = center.x - C.BOX_RADIUS - C.BOX_DIAMETER * x;
        float vy = center.y - C.BOX_RADIUS - C.BOX_DIAMETER * x;
        return new Vector2(vx, vy);
    }
    private void associateBoxes() {
        for (Box b : boxMap.values()) {
            int[] BMLocation = b.getBMLocation();
            int[] modifiers = {1, 0, 0, 1, -1, 0, 0, -1};
            for (int i = 0; i <= modifiers.length - 1; i += 2) {
                Box bb = boxMap.get((BMLocation[0]+modifiers[i])+","+(BMLocation[1]+modifiers[i+1]));
                if (bb == null)
                    continue;

                b.setAdjBox(DIRECTIONS[i/2], bb);
                bb.setAdjBox(DIRECTIONS[(i/2 + 2) % 4], b);
            }
        }
    }
}
