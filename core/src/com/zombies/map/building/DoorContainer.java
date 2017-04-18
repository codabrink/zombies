package com.zombies.map.building;

import com.badlogic.gdx.math.Vector2;

public class DoorContainer {
    private DoorFrame doorFrame;
    private Door door;

    public DoorContainer(Box b1, Box b2) {
        b1.doors.add(this);
        b2.doors.add(this);

        // TODO: improve box to door references
    }

    public DoorContainer(Vector2 p1, Vector2 p2, Building b) {
        doorFrame = new DoorFrame(p1, p2, b);
        //door      = new Door(p1, p2, b);
        Vector2 center = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    public DoorFrame getDoorFrame() { return doorFrame; }

    public void buildMesh(Vector2 center) {
        //door.rebuildModel();
    }
}
