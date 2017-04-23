package com.zombies.map.building.door;

import com.badlogic.gdx.math.Vector2;
import com.zombies.map.building.Box;
import com.zombies.map.building.Building;

public class DoorContainer {
    private DoorFrame doorFrame;
    private Door door;

    public DoorContainer(Box b1, Box b2) {
        b1.doors[b1.directionOf(b2)] = this;
        b2.doors[b2.directionOf(b1)] = this;
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
