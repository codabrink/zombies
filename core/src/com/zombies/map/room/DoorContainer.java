package com.zombies.map.room;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Modelable;

import java.util.HashMap;

public class DoorContainer {
    private DoorFrame doorFrame;

    public DoorContainer(Vector2 p1, Vector2 p2, Building b) {
        doorFrame = new DoorFrame(p1, p2, b);
        Vector2 center = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    public DoorFrame getDoorFrame() { return doorFrame; }

    public void buildMesh(Vector2 center) {
        doorFrame.buildMesh(center);
    }
}
