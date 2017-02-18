package com.zombies.map.room;

import com.badlogic.gdx.math.Vector2;
import com.zombies.Zone;
import com.zombies.interfaces.Modelable;

public class DoorContainer {
    private DoorFrame doorFrame;

    public DoorContainer(Vector2 p1, Vector2 p2, Modelable m) {
        doorFrame = new DoorFrame(p1, p2, m);
        Vector2 center = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        Zone.createHole(center, p1.dst(p2) / 2);
    }

    public DoorFrame getDoorFrame() { return doorFrame; }
}
